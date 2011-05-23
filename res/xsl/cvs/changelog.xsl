<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:RDF="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:chrome="http://www.mozilla.org/rdf/chrome#"
	version="1.0"
	xml:lang="ja-JP">

<xsl:output method="xml"
	indent="yes"
	/>

<xsl:param name="title"/>
<xsl:param name="module"/>
<xsl:param name="branch"/>
<xsl:param name="branchtag"/>
<xsl:param name="daysinpast"/>
<xsl:param name="start"/>
<xsl:param name="end"/>
<xsl:param name="N" xml:space="preserve">
</xsl:param>

<xsl:template match="/|*">
	<xsl:copy>
		<xsl:apply-templates select="@*"/>
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="@*|comment()|text()">
	<xsl:copy/>
</xsl:template>


<xsl:template match="changelog">
	<html>
	<head>
		<title><xsl:value-of select="$title"/></title>
		<link rel="stylesheet" href="changelog.css" type="text/css"/>
	</head>
	<body>
		<h1><xsl:value-of select="$title"/></h1>
		<dl class="entrylist">
			<xsl:apply-templates select=".//entry">
				<xsl:sort select="date" data-type="text" order="descending"/>
				<xsl:sort select="time" data-type="text" order="descending"/>
			</xsl:apply-templates>
		</dl>
	</body>
	</html>
</xsl:template>

<xsl:template match="entry">
	<dt>
		<xsl:value-of select="date"/><xsl:text> </xsl:text><xsl:value-of select="time"/>
		<xsl:text> by </xsl:text><xsl:value-of select="author"/>
	</dt>
	<dd>
		<p class="msg"><xsl:apply-templates select="msg"/></p>
		<ul class="filelist">
			<xsl:apply-templates select="file"/>
		</ul>
		
	</dd>
</xsl:template>

<xsl:template match="date">
	<span class="date"><xsl:value-of select="."/></span>
</xsl:template>

<xsl:template match="time">
	<span class="time"><xsl:value-of select="."/></span>
</xsl:template>

<xsl:template match="author">
	<span class="author">
		<a>
			<xsl:attribute name="href">mailto:<xsl:value-of select="."/></xsl:attribute>
			<xsl:value-of select="."/>
		</a>
	</span>
</xsl:template>

<xsl:template match="file">
	<xsl:variable name="lxr.url">
		<xsl:choose>
			<xsl:when test="starts-with($module, 'mozilla')">
				<xsl:value-of select="concat('http://lxr.mozilla.org/mozilla/source', substring-after($module, 'mozilla'), '/', name)"/>
			</xsl:when>
			<xsl:when test="starts-with($module, 'l10n')">
				<xsl:value-of select="concat('http://lxr.mozilla.org/l10n/source', substring-after($module, 'l10n'), '/', name)"/>
			</xsl:when>
			<!-- <xsl:when test="starts-with($module, 'mozilla')">
				<xsl:value-of select="concat('http://lxr.mozilla.org/', $branch, '/source', substring-after($module, 'mozilla'), '/', name)"/>
			</xsl:when>
			<xsl:when test="starts-with($module, 'l10n') and $branch = 'mozilla'">
				<xsl:value-of select="concat('http://lxr.mozilla.org/l10n/source', substring-after($module, 'l10n'), '/', name)"/>
			</xsl:when>
			<xsl:when test="starts-with($module, 'l10n') and $branch != 'mozilla'">
				<xsl:value-of select="concat('http://lxr.mozilla.org/l10n-', $branch, '/source', substring-after($module, 'l10n'), '/', name)"/>
			</xsl:when> -->
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="bonsai.url" select="concat('http://bonsai.mozilla.org/cvslog.cgi?file=', $module, '/', name)"/>
	<!-- <xsl:variable name="bonsai.url" select="concat('http://bonsai.mozilla.org/cvslog.cgi?file=', $module, '/', name, '&amp;rev=', $branchtag)"/> -->
	<li>
		<a href="{$lxr.url}"><xsl:value-of select="concat($module, '/', name)"/></a>
		<xsl:value-of select="concat(' (rev', revision, ')')"/>
		<!-- bonsai don't support l10n repository so far -->
		<xsl:if test="starts-with($module, 'mozilla')"> [<a href="{$bonsai.url}">bonsai</a>]</xsl:if>
	</li>
</xsl:template>

<xsl:template name="parsemsg">
	<xsl:param name="msg" select="."/><!-- msg element -->
	<xsl:choose>
		<xsl:when test="contains($msg, $N)">
			<xsl:value-of select="substring-before($msg, $N)"/>
			<br/><xsl:value-of select="$N"/>
			<xsl:call-template name="parsemsg">
				<xsl:with-param name="msg" select="substring-after($msg, $N)"/>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$msg"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="msg">
	<xsl:call-template name="parsemsg"/>
</xsl:template>


</xsl:stylesheet>
