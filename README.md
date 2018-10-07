# Bitbucket Filter Project Trait Plugin

Bitbucket Team/Project jobs can scan your Bitbucket organisation and create jobs based on repositories scanned.

This plugin adds two new filters to jobs with type Bitbucket Team/Project:

- Filter by Bitbucket project **key** using a regex expression
- Filter by Bitbucket project **name** using a regex expression

It is only useful for Bitbucket Cloud. 
Bitbucket Server organise repositories in a different way, making this filters not necessary.

With the use of the filters you can scan your whole organisation on Bitbucket Cloud, but only create jobs
for repositories that match a particular project.