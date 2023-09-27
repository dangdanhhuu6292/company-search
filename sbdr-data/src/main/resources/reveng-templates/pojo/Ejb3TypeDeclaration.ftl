<#if ejb3?if_exists>
<#if pojo.isComponent()>
@${pojo.importType("javax.persistence.Embeddable")}
<#else>

@XmlRootElement
// @org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes("application/*+json")
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@${pojo.importType("javax.persistence.Entity")}(name="<#if prefixEntity?has_content>${prefixEntity}</#if>${pojo.getDeclarationName()}")
@${pojo.importType("javax.persistence.Table")}(name="${clazz.table.name}"
<#if clazz.table.schema?exists && false> <!-- PFO: Do not want to select fixed catalog in code -->
    ,schema="${clazz.table.schema}"
</#if><#if clazz.table.catalog?exists && false> <!-- PFO: Do not want to select fixed catalog in code -->
    ,catalog="${clazz.table.catalog}"
</#if>
<#assign uniqueConstraint=pojo.generateAnnTableUniqueConstraint()>
<#if uniqueConstraint?has_content>
    , uniqueConstraints = ${uniqueConstraint} 
</#if>)
</#if>
</#if>