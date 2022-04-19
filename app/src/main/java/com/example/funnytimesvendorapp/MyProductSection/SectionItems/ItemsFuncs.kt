package com.example.funnytimesvendorapp.SectionItems

import com.example.funnytimesvendorapp.Models.FTProAttrContainer
import com.example.funnytimesvendorapp.Models.FTProAttribute

class ItemsFuncs {








    fun GetSelectedAttributesString(data:ArrayList<FTProAttrContainer>):String{
        var result = ""
        for (i in 0 until data.size) {
            for (a in 0 until data[i].ContainerAttributes.size) {
                if (data[i].ContainerAttributes[a].AttributeIsSelected){
                    result = result+"("+data[i].ContainerAttributes[a].AttrId+","+data[i].ContainerAttributes[a].AttrTypeId+")"
                }
            }
        }
        return result
    }


    fun GetSelectedAttributes(data:ArrayList<FTProAttrContainer>):ArrayList<FTProAttribute>{
        val result = ArrayList<FTProAttribute>()
        for (i in 0 until data.size) {
            for (a in 0 until data[i].ContainerAttributes.size) {
                if (data[i].ContainerAttributes[a].AttributeIsSelected){
                    data[i].ContainerAttributes[a].AttrName = data[i].ContainerName+":"+data[i].ContainerAttributes[a].AttrName
                    result.add(data[i].ContainerAttributes[a])
                }
            }
        }
        return result
    }

    fun GetProIdString(itemId: Int,data:ArrayList<FTProAttrContainer>):String{
        var result = itemId.toString()
        for (i in 0 until data.size) {
            for (a in 0 until data[i].ContainerAttributes.size) {
                if (data[i].ContainerAttributes[a].AttributeIsSelected){
                    result = result+"("+data[i].ContainerAttributes[a].AttrId+","+data[i].ContainerAttributes[a].AttrTypeId+")"
                }
            }
        }
        return result
    }

    fun GetProAttributesNames(attrbs:ArrayList<FTProAttribute>):String{
        var result = ""
        for (i in 0 until attrbs.size) {
            result = " "+result+attrbs[i].AttrName+" "
        }
        return result
    }





}