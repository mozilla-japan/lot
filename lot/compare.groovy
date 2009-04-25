// this script must be used within ant script
assert ant && project && properties && target && task && args

// Phase 0: Prepare Variables
debug = false
// mode = compare, merge (insert+clean), onlyfile, onlyentity, onlyaccesskey,
//        resetorder, resetheader, resetfooter, resetcomment, resetaccesskey
mode = [compare:args[0].contains('compare'),
	insert:args[0].contains('insert') || args[0].contains('merge'),
	clean:args[0].contains('clean')   || args[0].contains('merge'),
	withcomment:args[0].contains('withcomment'),
	onlyfile:args[0].contains('onlyfile'),
	onlyentity:args[0].contains('onlyentity') || args[0].contains('onlyaccesskey'),
	onlyaccesskey:args[0].contains('onlyaccesskey'),
	resetorder:args[0].contains('resetorder'),
	resetheader:args[0].contains('resetheader'),
	resetfooter:args[0].contains('resetfooter'),
	resetcomment:args[0].contains('resetcomment'),
	resetaccesskey:args[0].contains('resetaccesskey')]
if (mode.resetorder || mode.resetheader || mode.resetfooter || mode.resetcomment) ant.fail "Sorry, requested mode hasn't implemented yet."
locale1     = args[1]
locale2     = args[2]
dir1        = new File(args[3]).getCanonicalPath().replaceAll("\\\\", "/")
dir2        = new File(args[4]).getCanonicalPath().replaceAll("\\\\", "/")
excludes    = args[5]
output      = args[6]
// format = text, xml, html
format      = args[7]
failonerror = args[8] == 'true'
pattern = [
	properties: [header: properties.'RE.properties.header', entityblock: properties.'RE.properties.entityblock', footer: properties.'RE.properties.footer', l10ncomment: properties.'RE.properties.l10ncomment'],
	dtd: [header: properties.'RE.dtd.header', entityblock: properties.'RE.dtd.anyentityblock', footer: properties.'RE.dtd.footer', l10ncomment: properties.'RE.dtd.l10ncomment'],
	inc: [header: properties.'RE.inc.header', entityblock: properties.'RE.inc.entityblock', footer: properties.'RE.inc.footer', l10ncomment: properties.'RE.inc.l10ncomment'],
	ini: [header: properties.'RE.ini.header', entityblock: properties.'RE.ini.entityblock', footer: properties.'RE.ini.footer', l10ncomment: properties.'RE.ini.l10ncomment']
]
filetype = [
	properties: [encoding: 'UTF-8', pattern:pattern.properties, commentheader: "# ", commentfooter: ""],
	dtd: [encoding: 'UTF-8', pattern:pattern.dtd, commentheader: "<!-- ", commentfooter: " -->"],
	inc: [encoding: 'UTF-8', pattern:pattern.inc, commentheader: "#", commentfooter: ""],
	ini: [encoding: 'UTF-8', pattern:pattern.ini, commentheader: "#", commentfooter: ""]
]	
//l10n[basedir][filekey][entitykey] = [index, block, prespace:, precomment:, definition:, key:, value:, postcomment:]
l10n = [(dir1): [:], (dir2): [:], merged: [:]]
infomsg      = new StringBuilder()
fileerrmsg   = new StringBuilder()
entityerrmsg = new StringBuilder()
accesskeymsg = new StringBuilder()
mergelog     = new StringBuilder()
mergediff    = new StringBuilder()
metakeys = 3 // *info, *header, *footer
commonkeys = samekeys = uniquekeys1 = uniquekeys2 = 0

def isaccesskey(key) {
	key =~ properties.'compare.accesskey.pattern' && !(key =~ properties.'compare.accesskey.except')
}

def parse(dir, file, filetype, preprocess, postprocess) {
	filekey  = file.toString().replaceAll("\\\\", "/").replaceAll("$dir/","").replaceAll("/$locale1|$locale2","/*")
	l10n[dir][filekey] = ['*info':[dir:dir, file:file.toString().replaceAll("\\\\", "/"), filetype:filetype],
		'*header':[index:-1,key:'*header',block:''], '*footer':[index:-1,key:'*footer',block:'']]
	content = file.getFile().getText(filetype.encoding)
	if (content.contains("\r\n")) {
		infomsg << "\\r\\n found and replaced with \\n in:$dir/$filekey\n"
		content = content.replaceAll(/\r\n/, "\n")
	}
	original = content
	if (preprocess) content = preprocess(content)
	content.find(filetype.pattern.header) {
		l10n[dir][filekey]['*header'].block = it
		content = content[it.size()..<content.size()]
	}
	index = start = end = 0
	content.eachMatch(filetype.pattern.entityblock) { // don't use matcher to keep parser fast
		block, prespace, precomment, definition, key, value, postcomment ->
		start = end
		if (content[end..<end+block.size()] == block) {
			end += block.size()
		}
		else {
			content[end..<content.size()].find(/^((?s:.+?))($filetype.pattern.entityblock)/) {
				assert it[2] == block
				ant.echo "Ignoring invalid string in $dir/$filekey:\n${it[1]}"
				end += it[0].size()
			}
			l10n[dir][filekey]['*info'].isnotstrict = true // set flag not to edit this file
		}
		if (l10n[dir][filekey][key]) {
			entityerrmsg << "Duplicate Entities found in: $dir/$filekey:\n"
			entityerrmsg << "  ${l10n[dir][filekey][key].definition}\n  $definition\n\n"
			l10n[dir][filekey]["~$key"] = l10n[dir][filekey][key]
		}
		l10n[dir][filekey][key] = [index:index++, block:block, prespace:prespace, precomment:precomment, definition:definition, key:key, value:value, postcomment:postcomment]
		if (postprocess) l10n[dir][filekey][key] = postprocess(l10n[dir][filekey][key])
	}
	content = content[end..<content.size()]
	content.find(/^${filetype.pattern.footer}$/) {
		l10n[dir][filekey]['*footer'].block = it
		content = ''
	}
	if (content != '')  {
		ant.echo "Ignoring invalid footer in $dir/$file:\n$content"
		l10n[dir][filekey]['*info'].isnotstrict = true // set flag not to edit this file
	}
	if (debug) {
		temp = new StringBuilder()
		temp << l10n[dir][filekey]['*header'].block
		(0..<l10n[dir][filekey].size()-metakeys).each { i ->
			e = l10n[dir][filekey].find { k,v-> k[0] != '*' && v.index==i }
			if (e) temp << e.value.block
		}
		temp << l10n[dir][filekey]['*footer'].block
		if (original != temp.toString()) {
			ant.echo "parse error on: $file\n"
			ant.echo "[$original]\n\n[$temp]"
			ant.fail "parse error on: $file\n"
		}
	}
}

// Phase 1: Parse L10N Files
ant.fileset(dir:"$dir1", includes:'**/*.properties', excludes:excludes).each {
	parse(dir1, it, filetype.properties, null, null)
}
ant.fileset(dir:"$dir2", includes:'**/*.properties', excludes:excludes).each {
	parse(dir2, it, filetype.properties, null, null)
}
ant.fileset(dir:"$dir1", includes:'**/*.dtd', excludes:excludes).each {
	parse(dir1, it, filetype.dtd, null, null)
}
ant.fileset(dir:"$dir2", includes:'**/*.dtd', excludes:excludes).each {
	parse(dir2, it, filetype.dtd, null, null)
}
ant.fileset(dir:"$dir1", includes:'**/*.inc', excludes:excludes).each {
	parse(dir1, it, filetype.inc, null, null)
}
ant.fileset(dir:"$dir2", includes:'**/*.inc', excludes:excludes).each {
	parse(dir2, it, filetype.inc, null, null)
}
ant.fileset(dir:"$dir1", includes:'**/*.ini', excludes:excludes).each {
	parse(dir1, it, filetype.ini, null, null)
}
ant.fileset(dir:"$dir2", includes:'**/*.ini', excludes:excludes).each {
	parse(dir2, it, filetype.ini, null, null)
}

// to avoid ignoring existing blank file, check if it is null
l10n1 = l10n[dir1].groupBy { k,v -> l10n[dir2][k] != null ? 'common' : 'unique' }
l10n2 = l10n[dir2].groupBy { k,v -> l10n[dir1][k] != null ? 'common' : 'unique' }
// groupBy will not set empty map when nothing match and need to set empty map
l10n1.common = l10n1.common ?: [:]
l10n2.common = l10n2.common ?: [:]
l10n1.unique = l10n1.unique ?: [:]
l10n2.unique = l10n2.unique ?: [:]
assert l10n1.common.size() == l10n2.common.size()
infomsg << "Number of files in the $dir1 directory:\n"
infomsg << "  total: ${l10n[dir1].size()}, common: ${l10n1.common.size()}, unique: ${l10n1.unique.size()}\n\n"
infomsg << "Number of files in the $dir2 directory:\n"
infomsg << "  total: ${l10n[dir2].size()}, common: ${l10n2.common.size()}, unique: ${l10n2.unique.size()}\n\n"

// Phase 2: Unique L10N Files
if (l10n1.unique) {
	fileerrmsg << "File(s) only in $dir1 directory:\n"
	l10n1.unique.each { filekey,entities ->
		uniquekeys1 += entities.size()-metakeys // don't count header/footer
		fileerrmsg << "  $filekey \t(${entities.size()-metakeys} entities)\n"
		// Merge: copy new files
		if (mode.insert && !mode.onlyentity) {
			ant.echo "Copying new file: $filekey"
			newfile = l10n1.unique[filekey]['*info'].file.replaceAll("$dir1/","$dir2/").replaceAll("/$locale1","/$locale2")
			ant.copy(taskname: 'merge', file: "${l10n1.unique[filekey]['*info'].file}", tofile: newfile, overwrite: true, preservelastmodified: true)
			mergediff << "diff -u /dev/null $newfile".execute().in.getText(l10n1.unique[filekey]['*info'].filetype.encoding)
		}
	}
}
if (l10n2.unique) {
	fileerrmsg << "File(s) only in $dir2 directory:\n"
	l10n2.unique.each { filekey,entities ->
		uniquekeys2 += entities.size()-metakeys // don't count header/footer
		fileerrmsg << "  $filekey \t(${entities.size()-metakeys} entities)\n"
		// Merge: remove obsolate files
		if (mode.clean && !mode.onlyentity) {
			ant.echo "Removing obsolate file: $filekey"
			ant.move(taskname: 'merge', file: l10n2.unique[filekey]['*info'].file, tofile: "${l10n2.unique[filekey]['*info'].file}~", overwrite: true, preservelastmodified: true)
			mergediff << "diff -u ${l10n2.unique[filekey]['*info'].file} /dev/null".execute().in.getText(l10n2.unique[filekey]['*info'].filetype.encoding)
		}
	}
}

// Phase 3: Common L10N Files
l10n1.common.each { filekey, allentities1 ->
	allentities2 = l10n2.common[filekey]
	entities1 = allentities1.groupBy { k,v -> allentities2[k] != null ? 'common' : 'unique' }
	entities2 = allentities2.groupBy { k,v -> allentities1[k] != null ? 'common' : 'unique' }
	entities1.common = entities1.common ?: [:]
	entities2.common = entities2.common ?: [:]
	entities1.unique = entities1.unique ?: [:]
	entities2.unique = entities2.unique ?: [:]
	assert entities1.common.size() == entities2.common.size()
	commonkeys  += entities1.common.size() - entities1.unique.size()
	uniquekeys1 += entities1.unique.size()
	uniquekeys2 += entities2.unique.size()
	
	// Phase 3-1: Unique Entities
	if (entities1.unique || entities2.unique) {
		entityerrmsg << "Following ${entities1.unique.size()+entities2.unique.size()}/${entities1.common.size()-metakeys+entities1.unique.size()+entities2.unique.size()} Entities in this file don't match: $filekey:\n"
		if (entities1.unique) {
			entityerrmsg << "  ${entities1.unique.size()} entities only in $dir1/$filekey:\n"
			entities1.unique.each { key,block -> entityerrmsg << "    ${block.definition}\n" }
		}
		if (entities2.unique) {
			entityerrmsg << "  ${entities2.unique.size()} entities only in $dir2/$filekey:\n"
			entities2.unique.each { key,block -> entityerrmsg << "    ${block.definition}\n" }
		}
		entityerrmsg << "\n"
		
		// Pre-Merge: Insert New / Remove Obsolate Entities
		if (((entities1.unique && mode.insert) || (entities2.unique && mode.clean)) && !mode.onlyfile) {
			if (!l10n.merged[filekey]) l10n.merged[filekey] = allentities2
			// prepare list to keep the order of entities
			keylist = []
			(0..<l10n.merged[filekey].size()-metakeys).each { i ->
				e = l10n.merged[filekey].find{ k,v -> v.index==i }
				assert e != null // index must be continuous (all index must be found)
				keylist.push(e.key)
			}
			// find previous entity index and insert after it
			if (entities1.unique && mode.insert) {
				entities1.unique.sort{ l,r -> l.value.index <=> r.value.index }.each { key,block ->
					if (!mode.onlyaccesskey || isaccesskey(key)) {
						mergelog << "new entity $key will be inserted to: $filekey:\n"
						l10n.merged[filekey][key] = block
						if (mode.withcomment) {
							mergecomment = "${l10n2.common[filekey]['*info'].filetype.commentheader}MERGE NOTE: ${properties.'merge.comment'}${l10n2.common[filekey]['*info'].filetype.commentfooter}\n"
							l10n.merged[filekey][key].mergecomment = mergecomment
							l10n.merged[filekey][key].block = block.prespace+block.precomment+mergecomment+block.definition+block.postcomment
						}
						idx1 = block.index
						idx2 = -1
						while (--idx1 >= 0 && idx2 < 0) {
							prevkey = allentities1.find{ k,b -> b.index==idx1 }.key
							idx2 = keylist.indexOf(prevkey)
						}
						keylist.add(idx2+1,key)
					}
				}
			}
			if (entities2.unique && mode.clean) {
				// just remove from map and list
				entities2.unique.each { key,block ->
					if (!mode.onlyaccesskey || isaccesskey(key)) {
						mergelog << "obsolate entity $key will be removed from: $filekey:\n"
						l10n.merged[filekey].remove(key)
						keylist.remove(key)
					}
				}
			}
			// set new indices to each entity
			(0..<keylist.size()).each { i ->
				l10n.merged[filekey][keylist[i]].index = i
			}
		}
	}
	
	// Phase 3-2: Common Entities
	entities1.common.each { key,block1 ->
		block2 = entities2.common[key]
		// count changed (translated) entities
		if (block1.value == block2.value) samekeys++
		// Check Accesskey Entities
		if (isaccesskey(key) && block1.value != block2.value) {
			accesskeymsg << "Accesskey(s) in this file don't match: $filekey:\n"
			accesskeymsg << "  $key:  ${block1.value} != ${block2.value}\n"
			// Pre-Merge: Reset Accesskeys
			if (mode.resetaccesskey && !mode.onlyfile) {
				if (!l10n.merged[filekey]) l10n.merged[filekey] = allentities2
				mergelog << "$key accesskey in this file will be reset: $filekey:\n"
				// todo: better to replace only value part, not whole definition
				l10n.merged[filekey][key].definition = block1.definition
				l10n.merged[filekey][key].block = block2.prespace+block2.precomment+block1.definition+block2.postcomment
			}
		}
		// Pre-Merge: Reset Comment
		if (mode.resetcomment && !mode.onlyfile) {
			
			// Resetting all comments to en-US is useless
			// need to support 3 file merge
			
		}
	}
	
	// Phase 3-3: Output Merged File
	if (l10n.merged[filekey]) {
		if (l10n.merged[filekey]['*info'].isnotstrict) {
			mergelog << "\nCannot merge file (not supported format): $filekey\n"
		}
		else {
			mergelog << "Merging file: $filekey\n"
			// ToDo: confirm overwriting backup file
			ant.echo "Copying original file before merge: $filekey"
			ant.copy(taskname: 'backup', file: l10n.merged[filekey]['*info'].file, tofile: "${l10n.merged[filekey]['*info'].file}~", overwrite: true, preservelastmodified: true)
			content = new StringBuilder()
			content << l10n.merged[filekey]['*header'].block
			blocklist = []
			l10n.merged[filekey].each { k,v -> if (k[0] != '*') blocklist[v.index] = v.block }
			blocklist.each { content << it }
			content << l10n.merged[filekey]['*footer'].block
			new File(l10n.merged[filekey]['*info'].file).write(content.toString(), l10n.merged[filekey]['*info'].filetype.encoding)
			// output diff of original/merged files
			mergediff << "diff -u ${l10n.merged[filekey]['*info'].file}~ ${l10n.merged[filekey]['*info'].file}".execute().in.getText(l10n.merged[filekey]['*info'].filetype.encoding)
			//ant.exec(executable:'diff', output: , append: true) { arg(line: "-u ${l10n.merged[filekey]['*info'].file}~ ${l10n.merged[filekey]['*info'].file}") }
		}
	}
}

// Phase 4: Output Log/Diff etc

if (mergediff) {
	mergelog << "See ${output}.diff file to check merge diff.\n"
	new File("${output}.diff").append("$mergediff\n", 'UTF-8')
}


infomsg << "Total Number of entities in the $dir1 directory:\n"
infomsg << "  total: ${commonkeys+uniquekeys1}, common: $commonkeys, unique: $uniquekeys1\n\n"
infomsg << "Total Number of entities in the $dir2 directory:\n"
infomsg << "  total: ${commonkeys+uniquekeys2}, common: $commonkeys, unique: $uniquekeys2\n\n"
infomsg << "Total Number of same/different/unique entities in the two directory:\n"
infomsg << "  same: $samekeys (${Math.round(commonkeys?samekeys/commonkeys*100:0)}%), different: ${commonkeys-samekeys}, unique: ${uniquekeys1+uniquekeys2}\n\n"

ant.errorlog(type: 'compare', file: output, fail: failonerror && (fileerrmsg || entityerrmsg), 
	message: "Compare Locales Result:\n$infomsg\n\n$fileerrmsg\n\n$entityerrmsg\n\n$accesskeymsg\n\n$mergelog")


