/**
 * Copyright Javatar LLC 2018 ©
 * Licensed under the License located in the root of this repository (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://github.com/JavatarPro/declarative-pipeline/blob/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pro.javatar.pipeline.service.impl

import pro.javatar.pipeline.model.ReleaseInfo
import pro.javatar.pipeline.service.UiBuildService
import pro.javatar.pipeline.util.Logger

import static pro.javatar.pipeline.service.PipelineDslHolder.dsl

/**
 * @author Borys Zora
 * @since 2018-03-09
 */
class NpmBuildService extends UiBuildService {
    String moduleRepository
    protected String npmVersion
    protected String type
    String libraryFolder

    @Override
    void setUp() {
        Logger.debug("NpmBuildService setUp")
        Logger.debug("dsl.tool([name: ${npmVersion}, type: ${type}])")
        def node = dsl.tool([name: npmVersion, type: type])
        Logger.debug("node setup successfully")
        dsl.env.PATH="${node}/bin:${dsl.env.PATH}"
        // dsl.sh "npm config set registry ${moduleRepository}"
        dsl.sh 'node --version'
        dsl.sh 'npm -version'
        // cacheLibraryFolder()
        dsl.sh "npm install --no-save"
    }

    def cacheLibraryFolder() {
        String buildFolder = dsl.env.WORKSPACE
        String libraryCacheFolder = "${buildFolder}/${libraryFolder}"
        if (! dsl.fileExists(libraryCacheFolder)) {
            dsl.sh "mkdir ${libraryCacheFolder}"
        }
        Logger.debug("ln -s ${libraryCacheFolder} ${libraryFolder}")
        dsl.sh "ln -s ${libraryCacheFolder} ${libraryFolder}"
    }

    @Override
    void buildAndUnitTests(ReleaseInfo releaseInfo) {
        Logger.info('npm buildAndUnitTests start')
        dsl.sh "pwd; ls -la"
        if (!skipUnitTests) dsl.sh 'npm run test'
        dsl.sh 'npm run build'
        dsl.sh "zip -r ${getArtifact()} ${distributionFolder}"
        dsl.sh "pwd; ls -la"
        Logger.info('npm buildAndUnitTests end')
    }

    @Override
    String getCurrentVersion() {
        def packageJsonFile = dsl.readJSON file: 'package.json'
        return packageJsonFile.version
    }

    @Override
    def setupReleaseVersion(String releaseVersion) { // TODO delete, switch to setupVersion method
        Logger.info("setupReleaseVersion: ${releaseVersion} started")
        Logger.debug("package.json before change version")
        dsl.sh "cat package.json | grep version | grep -v flow"
        dsl.sh "npm --no-git-tag-version version ${releaseVersion}"
        Logger.debug("package.json after change version")
        dsl.sh "cat package.json | grep version | grep -v flow"
        Logger.info("setupReleaseVersion: ${releaseVersion} finished")
    }

    @Override
    def setupVersion(String version) {
        Logger.info("setupVersion: ${version} started")
        Logger.debug("package.json before change version")
        dsl.sh "cat package.json | grep version | grep -v flow"
        dsl.sh "npm --no-git-tag-version version ${version}"
        Logger.debug("package.json after change version")
        dsl.sh "cat package.json | grep version | grep -v flow"
        Logger.info("setupVersion: ${version} finished")
    }

    String archiveContent() {
        dsl.sh "zip -r dist.zip dist"
        return "dist.zip"
    }

    String getModuleRepository() {
        return moduleRepository
    }

    void setModuleRepository(String moduleRepository) {
        this.moduleRepository = moduleRepository
    }

    String getNpmVersion() {
        return npmVersion
    }

    void setNpmVersion(String npmVersion) {
        this.npmVersion = npmVersion
    }

    String getType() {
        return type
    }

    void setType(String type) {
        this.type = type
    }

    String getLibraryFolder() {
        return libraryFolder
    }

    void setLibraryFolder(String libraryFolder) {
        this.libraryFolder = libraryFolder
    }

    @Override
    public String toString() {
        return "NpmBuildService{" +
                "moduleRepository='" + moduleRepository + '\'' +
                ", npmVersion='" + npmVersion + '\'' +
                ", type='" + type + '\'' +
                ", libraryFolder='" + libraryFolder + '\'' +
                '}';
    }
}
