<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
xmlns:bn="http://www.bs.dk/standards/MarcXchange"
>
	<xsl:output method='html' indent='yes'/>

	<xsl:template match="bn:record">
		<font face='arial' size='2'>
			<p>
				<xsl:apply-templates select='./bn:leader'/>
				<xsl:apply-templates select='./bn:controlfield'/>
				<xsl:apply-templates select='./bn:datafield'/>
			</p>
		</font>
	</xsl:template>

	<xsl:template match="bn:leader">
		<b><xsl:text>Etiqueta de registo:</xsl:text></b>
		<xsl:text> </xsl:text>
		<xsl:value-of select="."/>
		<br/>
	</xsl:template>


	<xsl:template match="bn:controlfield">
		<b><xsl:value-of select="@bn:tag"/></b>
		<xsl:text> </xsl:text>
		<xsl:value-of select="."/>
		<br/>
	</xsl:template>


	<xsl:template match="bn:datafield">
		<b><xsl:value-of select="@bn:tag"/></b>
		<xsl:text> </xsl:text>
		<xsl:choose>
			<xsl:when test='@bn:ind1=" "'>
				<xsl:text>#</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@bn:ind1"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test='@bn:ind2=" "'>
				<xsl:text>#</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@bn:ind2"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text> </xsl:text>

		<xsl:apply-templates select="./bn:subfield"/>
		<br/>
	</xsl:template>


	<xsl:template match="bn:subfield">
		<b><xsl:text>$</xsl:text></b>
		<xsl:value-of select="@bn:code"/>
		<xsl:value-of select="."/>
	</xsl:template>
</xsl:stylesheet>





