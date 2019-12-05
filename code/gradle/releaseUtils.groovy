import java.util.regex.Matcher


void unSnapshotVersion() {
    def version = project.version.toString()
    def origVersion = version

    if (version.contains('-SNAPSHOT')) {
        project.ext.set('usesSnapshot', true)
        project.ext.set('snapshotVersion', version)
        version -= '-SNAPSHOT'
        updateVersionProperty(version)
    } else {
        project.ext.set('usesSnapshot', false)
    }
    println "Updated version from ${origVersion} to ${version}"
}


void updateVersion() {
    def version = project.version.toString()
    String pattern = /(\d+)([^\d]*$)/
    //noinspection GroovyUnusedAssignment
    Closure handler = { Matcher m, Project p -> m.replaceAll("${String.format('%02d', (m[0][1] as int) + 1)}${m[0][2]}") }
    Matcher matcher = version =~ pattern

    if (matcher.find()) {
        String nextVersion = handler(matcher, project)
        //if (project.properties['usesSnapshot']) {
        nextVersion += '-SNAPSHOT'
        //}

        nextVersion = getNextVersion(nextVersion);
        println "nextVersion is " + nextVersion

        project.ext.set("release.oldVersion", project.version)
        project.ext.set("release.newVersion", nextVersion)
        updateVersionProperty(nextVersion)
        return
    }

    throw new GradleException("Failed to increase version [$version] - unknown pattern")
}

String getNextVersion(String candidateVersion) {
    String nextVersion = project.properties['newVersion'];

    //if (useAutomaticVersion()) {
    return nextVersion ?: candidateVersion;
    //}

    //return readLine("Enter the next version (current one released as [${project.version}]):", nextVersion ?: candidateVersion);
}


/**
 * Updates properties file (<code>gradle.properties</code> by default) with new version specified.
 * If configured in plugin convention then updates other properties in file additionally to <code>version</code> property
 *
 * @param newVersion new version to store in the file
 */
void updateVersionProperty(String newVersion) {
    def oldVersion = "${project.version}"
    if (oldVersion != newVersion) {
        project.version = newVersion
        project.ext.set('versionModified', true)
        project.subprojects?.each { Project subProject ->
            subProject.version = newVersion
        }
        def versionProperties = ['version']
        def propFile = findPropertiesFile()
        versionProperties.each { prop ->
            try {
                project.ant.replace(file: propFile, token: "${prop}=${oldVersion}", value: "${prop}=${newVersion}", failOnNoReplacements: true)
            } catch (org.apache.tools.ant.BuildException be) {
                throw new GradleException("Unable to update version property. Please check file permissions, and ensure property is in \"${prop}=${newVersion}\" format.", be)
            }
        }
    }
}

File findPropertiesFile() {
    String versionPropertyFile = 'gradle.properties'
    File propertiesFile = project.file(versionPropertyFile)
    if (!propertiesFile.file) {
        if (!isVersionDefined()) {
            project.version = useAutomaticVersion() ? "1.0" : readLine("Version property not set, please set it now:", "1.0")
        }
        boolean createIt = project.hasProperty('version') && promptYesOrNo("[$propertiesFile.canonicalPath] not found, create it with version = ${project.version}")
        if (createIt) {
            propertiesFile.append("version=${project.version}")
        } else {
            log.debug "[$propertiesFile.canonicalPath] was not found, and user opted out of it being created. Throwing exception."
            throw new GradleException("[$propertiesFile.canonicalPath] not found and you opted out of it being created,\n please create it manually and specify the version property.")
        }
    }
    propertiesFile
}

