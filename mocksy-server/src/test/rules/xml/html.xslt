<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html"
            indent="yes"
            encoding="iso-8859-1"
            media-type="text/html"
            doctype-public="-//W3C//DTD HTML 4.0//EN"/>

<xsl:template match="/">
<ol>
<xsl:for-each select="parent/child">
	<li><xsl:value-of select="." /></li>
</xsl:for-each>
</ol>
</xsl:template>

</xsl:stylesheet>