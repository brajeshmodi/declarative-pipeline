/**
 * Copyright Javatar LLC 2018 ©
 * Licensed under the License located in the root of this repository (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://github.com/JavatarPro/pipeline-utils/blob/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pro.javatar.pipeline.stage

import org.codehaus.groovy.util.ReleaseInfo

import static pro.javatar.pipeline.service.PipelineDslHolder.dsl
/**
 * @author Borys Zora
 * @since 2018-03-09
 */
class BackwardCompatibilityTestStage extends Stage {

    ReleaseInfo info

    BackwardCompatibilityTestStage(ReleaseInfo info) {
        this.info = info
    }

    @Override
    void execute() throws Exception {
        dsl.echo "BackwardCompatibilityTestStage execute started: ${toString()}"
        dsl.timeout(time: 10, unit: 'MINUTES') {
            // todo: decide how to find previous release version
            // todo: and how pass it to next stages
        }
        dsl.echo "BackwardCompatibilityTestStage execute finished"
    }

    @Override
    String getName() {
        return "rollback"
    }

    @Override
    public String toString() {
        return "BackwardCompatibilityTestStage{" +
                ", info=" + info +
                '}';
    }
}