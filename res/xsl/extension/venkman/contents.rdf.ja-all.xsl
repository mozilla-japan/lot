<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:RDF="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:chrome="http://www.mozilla.org/rdf/chrome#"
	version="1.0"
	xml:lang="ja-JP">

<xsl:output method="xml"
	indent="no"
	/>

<!-- 
  <RDF:Description about="urn:mozilla:locale:ja"
        chrome:displayName="Japanese"
        chrome:name="ja"
        chrome:previewURL="http://www.mozilla.org/locales/ja.gif">
    <chrome:packages>
      <RDF:Seq about="urn:mozilla:locale:ja:packages">
        <RDF:li resource="urn:mozilla:locale:ja:venkman"/>
      </RDF:Seq>
    </chrome:packages>
  </RDF:Description>
  <RDF:Description about="urn:mozilla:locale:ja"
        chrome:displayName="Japanese"
        chrome:name="ja-JP-mac"
        chrome:previewURL="http://www.mozilla.org/locales/ja-JP-mac.gif">
    <chrome:packages>
      <RDF:Seq about="urn:mozilla:locale:ja-JP-mac:packages">
        <RDF:li resource="urn:mozilla:locale:ja-JP-mac:venkman"/>
      </RDF:Seq>
    </chrome:packages>
  </RDF:Description>
  <RDF:Description about="urn:mozilla:locale:ja"
        chrome:displayName="Japanese (Japan)"
        chrome:name="ja-JP"
        chrome:previewURL="http://www.mozilla.org/locales/ja-JP.gif">
    <chrome:packages>
      <RDF:Seq about="urn:mozilla:locale:ja-JP:packages">
        <RDF:li resource="urn:mozilla:locale:ja-JP:venkman"/>
      </RDF:Seq>
    </chrome:packages>
  </RDF:Description>
  <RDF:Description about="urn:mozilla:locale:ja"
        chrome:displayName="Japanese (Japan)"
        chrome:name="ja-JPM"
        chrome:previewURL="http://www.mozilla.org/locales/ja-JPM.gif">
    <chrome:packages>
      <RDF:Seq about="urn:mozilla:locale:ja-JPM:packages">
        <RDF:li resource="urn:mozilla:locale:ja-JPM:venkman"/>
      </RDF:Seq>
    </chrome:packages>
  </RDF:Description>
 -->

<xsl:template match="/|*">
	<xsl:copy>
		<xsl:apply-templates select="@*"/>
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="@*|comment()|text()">
	<xsl:copy/>
</xsl:template>



<!-- RDF:li 複数登録というのはディレクトリ名のせいかエラーになるので不可…
     でもそうしないと chrome.manifest が1つしか自動生成されない。うーん…
     取り敢えず chrome.manifest は別途生成することで対応か…
  <RDF:Seq about="urn:mozilla:locale:root">
    <RDF:li resource="urn:mozilla:locale:ja"/>
    <RDF:li resource="urn:mozilla:locale:ja-JP-mac"/>
    <RDF:li resource="urn:mozilla:locale:ja-JP"/>
    <RDF:li resource="urn:mozilla:locale:ja-JPM"/>
  </RDF:Seq>

 <xsl:template match="RDF:li[@resource='urn:mozilla:locale:ja' and ../@about='urn:mozilla:locale:root']">
	<xsl:copy-of select="."/>
	
	<xsl:text>
    </xsl:text>
	<xsl:copy>
		<xsl:apply-templates select="@*|*" mode="altlocale">
			<xsl:with-param name="oldlocale" select="'ja'"/>
			<xsl:with-param name="newlocale" select="'ja-JP-mac'"/>
		</xsl:apply-templates>
	</xsl:copy>
	
	<xsl:text>
    </xsl:text>
	<xsl:copy>
		<xsl:apply-templates select="@*|*" mode="altlocale">
			<xsl:with-param name="oldlocale" select="'ja'"/>
			<xsl:with-param name="newlocale" select="'ja-JP'"/>
		</xsl:apply-templates>
	</xsl:copy>
	
	<xsl:text>
    </xsl:text>
	<xsl:copy>
		<xsl:apply-templates select="@*|*" mode="altlocale">
			<xsl:with-param name="oldlocale" select="'ja'"/>
			<xsl:with-param name="newlocale" select="'ja-JPM'"/>
		</xsl:apply-templates>
	</xsl:copy>
</xsl:template> -->

<xsl:template match="RDF:Description[@about='urn:mozilla:locale:ja' and @chrome:name='ja']">
	<xsl:copy-of select="."/>
	
	<xsl:text>
  </xsl:text>
	<xsl:copy>
		<xsl:copy-of select="@about"/>
		<xsl:apply-templates select="@*[local-name()!='about']" mode="altlocale">
			<xsl:with-param name="oldlocale" select="'ja'"/>
			<xsl:with-param name="newlocale" select="'ja-JP-mac'"/>
		</xsl:apply-templates>
		<xsl:apply-templates mode="altlocale">
			<xsl:with-param name="oldlocale" select="'ja'"/>
			<xsl:with-param name="newlocale" select="'ja-JP-mac'"/>
		</xsl:apply-templates>
	</xsl:copy>
	
	<xsl:text>
  </xsl:text>
	<xsl:copy>
		<xsl:copy-of select="@about"/>
		<xsl:apply-templates select="@*[local-name()!='about']" mode="altlocale">
			<xsl:with-param name="oldlocale" select="'ja'"/>
			<xsl:with-param name="newlocale" select="'ja-JP'"/>
		</xsl:apply-templates>
		<xsl:apply-templates mode="altlocale">
			<xsl:with-param name="oldlocale" select="'ja'"/>
			<xsl:with-param name="newlocale" select="'ja-JP'"/>
		</xsl:apply-templates>
	</xsl:copy>
	
	<xsl:text>
  </xsl:text>
	<xsl:copy>
		<xsl:copy-of select="@about"/>
		<xsl:apply-templates select="@*[local-name()!='about']" mode="altlocale">
			<xsl:with-param name="oldlocale" select="'ja'"/>
			<xsl:with-param name="newlocale" select="'ja-JPM'"/>
		</xsl:apply-templates> -->
		<xsl:apply-templates mode="altlocale">
			<xsl:with-param name="oldlocale" select="'ja'"/>
			<xsl:with-param name="newlocale" select="'ja-JPM'"/>
		</xsl:apply-templates>
	</xsl:copy>
</xsl:template>



<xsl:template match="node()" mode="altlocale">
	<xsl:param name="oldlocale" select="'ja'"/>
	<xsl:param name="newlocale" select="'ja-JP-mac'"/>
	
	<xsl:copy>
		<xsl:apply-templates select="@*" mode="altlocale">
			<xsl:with-param name="oldlocale" select="$oldlocale"/>
			<xsl:with-param name="newlocale" select="$newlocale"/>
		</xsl:apply-templates>
		<xsl:apply-templates mode="altlocale">
			<xsl:with-param name="oldlocale" select="$oldlocale"/>
			<xsl:with-param name="newlocale" select="$newlocale"/>
		</xsl:apply-templates>
	</xsl:copy>
</xsl:template>
<xsl:template match="@*" mode="altlocale">
	<xsl:param name="oldlocale" select="'ja'"/>
	<xsl:param name="newlocale" select="'ja-JP-mac'"/>
	<xsl:variable name="oldlocale_urn" select="concat('urn:mozilla:locale:', $oldlocale)"/>
	<xsl:variable name="oldlocale_gif" select="concat('/', $oldlocale, '.gif')"/>
	<xsl:variable name="oldlocale_png" select="concat('/', $oldlocale, '.png')"/>
	
	<xsl:choose>
		<xsl:when test=". = $oldlocale"><!-- "ja" - "ja-JP-mac" -->
			<xsl:attribute name="{local-name()}">
				<xsl:value-of select="$newlocale"/>
			</xsl:attribute>
		</xsl:when>
		<xsl:when test=". = $oldlocale_urn"><!-- "urn:mozilla:locale:ja" - "urn:mozilla:locale:ja-JP-mac" -->
			<xsl:attribute name="{local-name()}">
				<xsl:value-of select="concat('urn:mozilla:locale:', $newlocale)"/>
			</xsl:attribute>
		</xsl:when>
		<xsl:when test="starts-with(., concat($oldlocale_urn, ':'))"><!-- "urn:mozilla:locale:ja:*" - "urn:mozilla:locale:ja-JP-mac:" -->
			<xsl:attribute name="{local-name()}">
				<xsl:value-of select="concat('urn:mozilla:locale:', $newlocale, substring-after(., $oldlocale_urn))"/>
			</xsl:attribute>
		</xsl:when>
		<xsl:when test="contains(., $oldlocale_gif)"><!-- "/ja.gif" -->
			<xsl:attribute name="{local-name()}">
				<xsl:value-of select="concat(substring-before(., $oldlocale_gif), '/', $newlocale, '.gif', substring-after(., $oldlocale_gif))"/>
			</xsl:attribute>
		</xsl:when>
		<xsl:when test="contains(., $oldlocale_png)"><!-- "/ja.gif" -->
			<xsl:attribute name="{local-name()}">
				<xsl:value-of select="concat(substring-before(., $oldlocale_png), '/', $newlocale, '.png', substring-after(., $oldlocale_png))"/>
			</xsl:attribute>
		</xsl:when>
		
		<xsl:otherwise>
			<xsl:copy/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>



</xsl:stylesheet>
