// this script must be used within ant script
assert ant && project && properties && target && task && args

// Phase 0: Prepare Variables
debug       = false
mode        = [compare:args[0].contains('compare'), insert:args[0].contains('insert')||args[0].contains('merge'), clean:args[0].contains('clean')||args[0].contains('merge'), order:args[0].contains('order'), resetcomment:args[0].contains('resetcomment'), resetaccesskey:args[0].contains('resetaccesskey')]
locale1     = args[1]
locale2     = args[2]
dir1        = new File(args[3]).getCanonicalPath()
dir2        = new File(args[4]).getCanonicalPath()
excludes    = args[5]
output      = args[6]
failonerror = args[7] == 'true'
propfilepattern   = /${properties.'RE.properties.file'}/
dtdfilepattern    = /${properties.'RE.dtd.file'}/
propentitypattern = /${properties.'RE.properties.entityblock'}/
dtdentitypattern  = /${properties.'RE.dtd.anyentityblock'}/
//l10n[basedir][filekey][entitykey] = [prespace:, precomment:, definition:, key:, value:, postcomment:]
l10n = [(dir1): [:], (dir2): [:]]
infomsg      = new StringBuilder()
fileerrmsg   = new StringBuilder()
entityerrmsg = new StringBuilder()
accesskeymsg = new StringBuilder()
mergelog     = new StringBuilder()
metakeys = 3 // *info, *header, *footer
commonkeys = uniquekeys1 = uniquekeys2 = 0

def parse(dir, file, encoding, filepattern, entitypattern, preprocess, postprocess) {
	filekey  = file.toString().replaceAll("$dir/","").replaceAll("/$locale1|$locale2","/*")
	l10n[dir][filekey] = ['*info':[dir:dir, file:file, encoding:encoding],
		'*header':[index:-1,key:'*header'], '*footer':[index:-1,key:'*footer']]
	content = file.getFile().getText(encoding)
	if (content.contains("\r\n")) {
		infomsg << "\\r\\n found and replaced with \\n in:$dir/$filekey\n"
		content = content.replaceAll(/\r\n/, "\n")
	}
	if (preprocess) content = preprocess(content)
	matcher = content =~ filepattern
	if (matcher.count > 0) {
		//assert content.length() == matcher[0][1].length() + matcher[0][2].length() + matcher[0][matcher.groupCount()].length()
		//assert content == matcher[0][1]+matcher[0][2]+matcher[0][matcher.groupCount()]
		content = matcher[0][2]
		l10n[dir][filekey]['*header'].block = matcher[0][1]
		l10n[dir][filekey]['*footer'].block = matcher[0][matcher.groupCount()]
	}
	else {
		ant.echo "$file don't match with file pattern (syntax error?)"
		l10n[dir][filekey]['*info'].isnotstrict = true // set flag not to edit this file
	}
	
	index = 0
	content.eachMatch(entitypattern) {
		block, prespace, precomment, definition, key, value, postcomment ->
		if (l10n[dir][filekey][key]) {
			entityerrmsg << "Duplicate Entities found in: $filekey:\n"
			entityerrmsg << "  ${l10n[dir][filekey][key].definition}\n  $definition\n\n"
			l10n[dir][filekey]["~$key"] = l10n[dir][filekey][key]
		}
		l10n[dir][filekey][key] = [index:index++, block:block, prespace:prespace, precomment:precomment, definition:definition, key:key, value:value, postcomment:postcomment]
		if (postprocess) l10n[dir][filekey][key] = postprocess(l10n[dir][filekey][key])
	}
	if (debug) {
		temp = new StringBuilder()
		(0..<l10n[dir][filekey].size()).each { i ->
			b = l10n[dir][filekey].find { k,v-> k[0] != '*' && v.index==i }
			if (b) temp << b.getValue().block
		}
		if (content != temp.toString()) {
			ant.echo "parse error on: $file\n"
			ant.echo "[$content]\n\n[$temp]"
		}
	}
}

// Phase 1: Parse L10N Files
ant.fileset(dir: "$dir1", includes:'**/*.properties', excludes:excludes).each {
	parse(dir1, it, 'UTF-8', propfilepattern, propentitypattern, null, null)
}
ant.fileset(dir: "$dir2", includes:'**/*.properties', excludes:excludes).each {
	parse(dir2, it, 'UTF-8', propfilepattern, propentitypattern, null, null)
}
ant.fileset(dir: "$dir1", includes:'**/*.dtd', excludes:excludes).each {
	parse(dir1, it, 'UTF-8', dtdfilepattern, dtdentitypattern, null, null)
}
ant.fileset(dir: "$dir2", includes:'**/*.dtd', excludes:excludes).each {
	parse(dir2, it, 'UTF-8', dtdfilepattern, dtdentitypattern, null, null)
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
	}
	// Merge: copy new files
	
}
if (l10n2.unique) {
	fileerrmsg << "File(s) only in $dir2 directory:\n"
	l10n2.unique.each { filekey,entities ->
		uniquekeys2 += entities.size()-metakeys // don't count header/footer
		fileerrmsg << "  $filekey \t(${entities.size()-metakeys} entities)\n"
	}
	// Merge: remove obsolate files
	
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
		
		// Merge: merge new/obsolate entities
		
		
	}
	
	// Phase 3-2: Common Entities
	entities1.common.each { key,block1 ->
		block2 = entities2.common[key]
		// Check Accesskey Entities
		if (key =~ properties.'compare.accesskey.pattern' &&
			!(key =~ properties.'compare.accesskey.except') &&
			block1.value != block2.value) {
			accesskeymsg << "Accesskey(s) in this file don't match: $filekey:\n"
			accesskeymsg << "  $key:  ${block1.value} != ${block2.value}\n"
			// Reset: reset accesskeys
			
		}
		// Check Comment Updates
		
		
	}
	// Merge: finally output merged files
	
	
}

infomsg << "Total Number of entities in the $dir1 directory:\n"
infomsg << "  total: ${commonkeys+uniquekeys1}, common: $commonkeys, unique: $uniquekeys1\n\n"
infomsg << "Total Number of entities in the $dir2 directory:\n"
infomsg << "  total: ${commonkeys+uniquekeys2}, common: $commonkeys, unique: $uniquekeys2\n\n"

ant.errorlog(type: 'compare', file: output, fail: failonerror && (fileerrmsg || entityerrmsg), 
	message: "Compare Locales Result:\n$infomsg\n\n$fileerrmsg\n\n$entityerrmsg\n\n$accesskeymsg")
