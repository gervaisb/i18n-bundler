
i18n bundler - API Documentaion
===============================

# 1. Generalities
This API is designed to work with JSon only.

# 2. Ressources
The api expose 4 ressources linkeds togethers.
    Project (groupId, artifactId)
    Bundle (name)
    Translation (key, values[:lang])

## 2.1 Project
### Responses structures
List :
    projects = [
    	{href : Url
    	 groupId : String with pattern ([a-z0-9_-].?)+
    	 artifactId : String with pattern ([a-z0-9_-].?)+ }
    ]
 
Details :
    project = {
    	self : Url
    	groupId	: String with pattern ([a-z0-9_-].?)+
    	artifactId : String with pattern ([a-z0-9_-].?)+
    	bundles = [
    		{name : String
    		 href : Url
    		 lines : Int}
    	]
    }
    
### URLS
#### GET '/'
Return a list of all projects.
The response state is always 200 even is there is no element to display his body has the List format.

#### GET '/{groupId}:{artifactId}'
Show a project.
The response state can be 200_Ok or 404_BadRequest his body has the Details format.

#### PUT '/{groupId}:{artifactId}'
Create a new project. The request must be blank since the project contains only the 'groupId' and 'artifactId'.
The response states must be '201_Created' or '400_BadRequest' and the body has the Details format.


## 2.2 Bundle

## 2.3 Translation

Details :
	translation = {
		key : String
		values = [
			lang : value
		]
	}
