<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <!--ROOT NODE 'SITE'-->
    <xsl:template match="/">
      
        <html>
            <head></head>
          
            <body>
                <!-- 1) Site information -->
                <table border="1" cellpadding="2" 
                    style="font-weight: bold;" width="30%">
                    <tr>
                        <td bgcolor="#B5D5FF" width="20%">Site Name:</td>
                        <td> <xsl:value-of select="site/@title"/></td>
                    </tr>
                    <tr>
                        <td bgcolor="#B5D5FF" width="20%">Site ID:</td>
                        <td><xsl:value-of select="site/@id"/></td>
                    </tr>
                   
                </table>
                <br/><br/> 
                
                <!-- 2) Matrix User Information  -->
                
                <xsl:apply-templates />
                
            </body>             
        </html>
    </xsl:template>
        
    <!--2) Matrix Scaffolding Information -->
    
    <xsl:template match="user">
        <xsl:variable name="matrix_user_id" select="current()/@id"/>
       
        <table border="1" cellpadding="2" 
            style="font-weight: bold;" width="30%">
                <tr>
                    <td bgcolor="#B5D5FF" width="20%">User:</td>
                    <td><xsl:value-of select="current()/@pid"/></td>
                </tr>       
        </table>
        
        <!--2) Matrix Scaffolding Information -->
        
        <xsl:for-each select="scaffolding">
            <xsl:variable name="matrix_scaffolding_id" select="current()/@id"/>
            <table border="1" cellpadding="2" 
                style="font-weight: bold;" width="30%">
                <tr>
                    <td  bgcolor="#B5D5FF" width="20%">Matrix: </td>
                    <td><xsl:value-of select="current()/@title"/></td>
                </tr>
            </table>
            <br/>
            
         <!--3) Matrix Detail Information -->  
            <xsl:apply-templates select="//matrix[@saffolding-id=$matrix_scaffolding_id and @user-id=$matrix_user_id]">  
                <xsl:with-param name="matrix_scaffolding_id" select="$matrix_scaffolding_id"/>
                <xsl:with-param name="matrix_user_pid" select="$matrix_user_id"/>
            </xsl:apply-templates> 
            
        </xsl:for-each>
            
   </xsl:template>
    
    <!-- Category Matrices with different matrix shell -->
    <xsl:key name="scaffoldingkey" match="/matrix" use="scaffoldingId"/>
    
    <!-- display Matrix information -->
    <xsl:template match="//matrix">    
        <xsl:param name="matrix_scaffolding_id"/>
        <xsl:variable name="parent_matrix_id" select="current()/@id"/>
        <!--<h2 bgcolor="#e6e6e6"><xsl:value-of select="current()/@id"/></h2> -->
        
        <xsl:apply-templates select="//matrix-cell[../@id=$parent_matrix_id]">  
            <xsl:with-param name="parent_matrix_id" select="$parent_matrix_id"/>
            <xsl:sort select="concat(rowSeq, colSeq)" data-type="number" /> 
        </xsl:apply-templates> 
        
    </xsl:template> 
        
    <!--4 Matrix Cell information -->
    <xsl:template match="//matrix-cell">    
        <xsl:param name="parent_matrix_id"/>
        <xsl:variable name="matrix_cell_id" select="current()/@id"/>
        
        <!--check maxrow,col of the matrix -->
        <!--<xsl:if test="position() = last()">
            <xsl:text>MaxColumn: </xsl:text><xsl:value-of select="colSeq" />
            <xsl:text>MaxRow: </xsl:text><xsl:value-of select="rowSeq" />
        </xsl:if> -->
       
        <br/>
        <li style="list-style-type:none"><strong>Cell (</strong><xsl:value-of select="rowSeq"/><xsl:text>, </xsl:text><xsl:value-of select="colSeq"/><strong>)</strong></li>
        <li><strong>Name:   </strong><xsl:value-of select="current()/@name"/></li>
        <li><strong>Row Detail Description:   </strong></li>
       <!-- <xsl:apply-templates select="row-desc"></xsl:apply-templates> -->
        <xsl:apply-templates select="//row[../@id=$matrix_cell_id and (row-long-description)]"/> 
        
        <li><strong>Status:   </strong><xsl:value-of select="status"/></li>
        
        <li><strong>Description:   </strong></li>
        <xsl:apply-templates select="description"></xsl:apply-templates>
        
        <li><strong>Instructions:   </strong></li>
        <xsl:apply-templates select="instruction/text()"></xsl:apply-templates>
        
        <!-- Matrix Cell Detail Contents: Items (Forms, Attachments), Feedback, Evaluations -->
        
        <li style="font-weight: bold; color:#4d4d4d"><xsl:text>Forms:   </xsl:text></li>
        <!--apply only if there's attachments for the matrixcell-->
        <xsl:apply-templates select="//forms[../../@id=$matrix_cell_id and (form)]"/>

        <li style="font-weight: bold; color:#4d4d4d"><xsl:text>Attachments:   </xsl:text></li>
        <xsl:apply-templates select="//attachments[../../@id=$matrix_cell_id and (attachment)]"/>
         
        <li style="font-weight: bold; color:#4d4d4d"><xsl:text>Feedback:  </xsl:text></li>
        <xsl:apply-templates select="//feedback[../@id=$matrix_cell_id]"/> 
        
        <li style="font-weight: bold; color:#4d4d4d"><xsl:text>Evaluations:   </xsl:text></li>
        <xsl:apply-templates select="//evaluations[../@id=$matrix_cell_id and (evaluation)]"/>
        <br/>
    </xsl:template>
    
    <xsl:template match="description">
        <xsl:value-of select="." disable-output-escaping="yes" />
    </xsl:template>
    
    <xsl:template match="instruction/text()">
        <xsl:value-of select="." disable-output-escaping="yes" />
    </xsl:template>
    
    <xsl:template match="//row/*">
        <xsl:value-of select="." disable-output-escaping="yes" />
    </xsl:template>
    
    
    <xsl:template match="//attachments/*">
       <a target="_blank" href="{concat('./',resource/file-path)}">
            <xsl:value-of select="resource/display-name"/></a> 
        <br/>
    </xsl:template>
    
    <xsl:template match="//forms/*">
        <a target="_blank" href="{concat('./',resource/file-path)}">
            <xsl:value-of select="resource/display-name"/></a> 
        <br/>
    </xsl:template>
    
    <xsl:template match="//evaluations/*">
        <a target="_blank" href="{concat('./',resource/file-path)}">
            <xsl:value-of select="resource/display-name"/></a> 
        <br/>
    </xsl:template>
    
 <xsl:template match="//feedback/*">
        <a target="_blank" href="{concat('./',resource/file-path)}">
            <xsl:value-of select="resource/display-name"/></a> 
        <br/>
    </xsl:template>
    
    
    
    
    
    
    
    
    
    
    
    
    
    
         
</xsl:stylesheet>