<#-- // Property accessors -->
<#foreach property in pojo.getAllPropertiesIterator()>
<#if pojo.getMetaAttribAsBool(property, "gen-property", true)>
 <#if pojo.hasFieldJavaDoc(property)>    
    /**       
     * ${pojo.getFieldJavaDoc(property, 4)}
     */
</#if>
    <#include "GetPropertyAnnotation.ftl"/>
    ${pojo.getPropertyGetModifiers(property)} ${pojo.getJavaTypeName(property, jdk5)} ${pojo.getGetterSignature(property)}() {
        return this.${property.name};
    }
    
    ${pojo.getPropertySetModifiers(property)} void set${pojo.getPropertyName(property)}(${pojo.getJavaTypeName(property, jdk5)} ${property.name}) {
        this.${property.name} = ${property.name};
    }

    <#if c2h.isOneToOne(property) || c2h.isManyToOne(property)>


    @Transient
    <#assign referencedEntity = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))/>
    <#assign referencedPK = referencedEntity.identifierProperty/>
    <#assign referencedType = referencedEntity.getJavaTypeName(referencedPK, jdk5)/>
    <#if referencedType.equals("int")><#assign referencedType = "Integer"/></#if>
    ${pojo.getPropertyGetModifiers(property)} ${referencedType} ${pojo.getGetterSignature(property)}${pojo.getPropertyName(referencedPK)}() {
        return this.${property.name} == null ? null : this.${property.name}.get${pojo.getPropertyName(referencedPK)}();
    }
	<#if 1 == 2>
    @Transient
    <#assign referencedEntity = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))/>
    <#assign referencedPK = referencedEntity.identifierProperty/>
    ${pojo.getPropertyGetModifiers(property)} ${referencedEntity.getJavaTypeName(referencedPK, jdk5)} ${pojo.getGetterSignature(property)}${pojo.getPropertyName(referencedPK)}() {
        return this.${property.name} == null ? null : this.${property.name}.get${pojo.getPropertyName(referencedPK)}();
    }
    </#if>

    ${pojo.getPropertySetModifiers(property)} void set${pojo.getPropertyName(property)}${pojo.getPropertyName(referencedPK)}(${referencedEntity.getJavaTypeName(referencedPK, jdk5)} ${referencedPK.name}) {
    <#if !referencedEntity.getJavaTypeName(referencedPK, jdk5).equals("int") &&
         !referencedEntity.getJavaTypeName(referencedPK, jdk5).equals("char") &&
         !referencedEntity.getJavaTypeName(referencedPK, jdk5).equals("byte") &&
         !referencedEntity.getJavaTypeName(referencedPK, jdk5).equals("short") &&
         !referencedEntity.getJavaTypeName(referencedPK, jdk5).equals("long") &&
         !referencedEntity.getJavaTypeName(referencedPK, jdk5).equals("float") &&
         !referencedEntity.getJavaTypeName(referencedPK, jdk5).equals("double")
    >
        if (${referencedPK.name} == null) {
            this.${property.name} = null;
        } else {
            ${pojo.getJavaTypeName(property, jdk5)} obj = new ${pojo.getJavaTypeName(property, jdk5)}();
            obj.set${pojo.getPropertyName(referencedPK)}(${referencedPK.name});
            this.${property.name} = obj;
        }
    <#else>
        ${pojo.getJavaTypeName(property, jdk5)} obj = new ${pojo.getJavaTypeName(property, jdk5)}();
        obj.set${pojo.getPropertyName(referencedPK)}(${referencedPK.name});
        this.${property.name} = obj;
    </#if>
    }

    </#if>
</#if>
</#foreach>
