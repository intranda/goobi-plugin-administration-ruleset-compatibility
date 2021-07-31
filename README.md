# Goobi workflow Plugin: goobi-plugin-administration-sample

<img src="https://goobi.io/wp-content/uploads/logo_goobi_plugin.png" align="right" style="margin:0 0 20px 20px;" alt="Plugin for Goobi workflow" width="175" height="109">

This is the Sample Goobi administration plugin. It is used as a reference implementation for future administration plugins. It does contain a configuration file and a simple user interface.

This is a plugin for Goobi workflow, the open source workflow tracking software for digitisation projects. More information about Goobi workflow is available under https://goobi.io. If you want to get in touch with the user community simply go to https://community.goobi.io.

## KICKSTART

To start using this Sample Plugin as template, simply do the following:

### Create the repository

- Open https://gitea.intranda.com/repo/create to create a new repository
- Select as template `goobi-workflow/goobi-plugin-administration-sample`
- Select as `Owner` the value `goobi-workflow`
- Define a `Repository name` based on convention, e.g.  `goobi-plugin-administration-myPlugin`
- Click in `Template Item` the checkbox `Git Content (Default branch)`
- Click on Button `Create Repository`

### Checkout and rename project

- Open Eclipse and checkout the new created Repository. You will find a new project with the name `A-goobi-plugin-administration-sample` there.
- Right-click on the new project and refactor/rename it to your plugin name, e.g. `goobi-plugin-administration-myPlugin`
- Open the ant file `build.xml` and define the plugin name there in the property `name`, e.g. replace `sample` with `myPlugin`
- Save the ant file and right-click in the outline view on the target `rename` to start renaming all configuration files, java classes and GUI files
- Right-click on the project and click `Refresh` to see the renaming results.

### Update Readme file

- The plugin is now created and ready to be used. Remember to commit your changes and don't forget to update the README.md file that is located on folder upwards of the eclipse project. As it does not get committed by Eclipse you can commit it with these commands:

  ```
  cd ~/git/goobi-plugin-administration-myPlugin
  git commit -am "updated readme"
  git push
  ```

## Plugin details

More information about the functionality of this plugin and the complete documentation can be found in the central documentation area at https://docs.goobi.io

Detail | Description
--- | ---
**Plugin identifier**       | intranda_administration_sample
**Plugin type**             | Administration plugin
**Licence**                 | GPL 2.0 or newer
**Documentation (German)**  | - no documentation available -
**Documentation (English)** | - no documentation available -

## Goobi details

Goobi workflow is an open source web application to manage small and large digitisation projects mostly in cultural heritage institutions all around the world. More information about Goobi can be found here:

Detail | Description
--- | ---
**Goobi web site**  | https://www.goobi.io
**Twitter**         | https://twitter.com/goobi
**Goobi community** | https://community.goobi.io

## Development

This plugin was developed by intranda. If you have any issues, feedback, question or if you are looking for more information about Goobi workflow, Goobi viewer and all our other developments that are used in digitisation projects please get in touch with us.  

Contact | Details
--- | ---
**Company name**  | intranda GmbH
**Address**       | Bertha-von-Suttner-Str. 9, 37085 Göttingen, Germany
**Web site**      | https://www.intranda.com
**Twitter**       | https://twitter.com/intranda
