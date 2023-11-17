<?xml version="1.0" encoding="UTF-8"?>
<!--

    Java XSL code library

    Copyright (C) 2010 Benoit Mercier <info@servicelibre.com> — All rights reserved.

    This file is part of jxsl.

    jxsl is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, version 3.

    jxsl is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with jxsl.  If not, see <http://www.gnu.org/licenses/>.

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

    <xsl:output
            name="html"
            method="html"
            indent="yes"
            omit-xml-declaration="yes"
            encoding="UTF-8" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
            exclude-result-prefixes="#all" />

    <xsl:template match="/">
        <xsl:text>MAIN OUTPUT</xsl:text>
        <xsl:call-template name="surname"/>
    </xsl:template>

    <xsl:template name="surname">
        <xsl:result-document format="html" href="surnames.html">
        <!--xsl:result-document format="html" href="{resolve-uri('surnames.html', static-base-uri())}"-->
            <html>
                <head><title>surnames</title></head>
                <body>
                    <xsl:for-each select="//surname">
                        <p><xsl:value-of select="."/></p>
                    </xsl:for-each>
                </body>
            </html>
        </xsl:result-document>
    </xsl:template>

</xsl:stylesheet>
