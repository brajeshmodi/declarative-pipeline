package pro.javatar.pipeline.builder

import groovy.text.GStringTemplateEngine
import pro.javatar.pipeline.Flow
import pro.javatar.pipeline.builder.converter.FlowBuilderConverter
import pro.javatar.pipeline.builder.converter.JenkinsBuildParamsConverter
import pro.javatar.pipeline.builder.converter.YamlConverter
import pro.javatar.pipeline.builder.model.YamlConfig
import pro.javatar.pipeline.service.PipelineDslHolder
import pro.javatar.pipeline.util.Logger

import static pro.javatar.pipeline.service.PipelineDslHolder.dsl

class YamlFlowBuilder {

    static final DEFAULT_CONFIG_FILE = "declarative-pipeline.yml"

    String configFile

    YamlConverter yamlConverter = new YamlConverter()

    FlowBuilderConverter flowBuilderConverter = new FlowBuilderConverter()

    JenkinsBuildParamsConverter jenkinsBuildParamsConverter = new JenkinsBuildParamsConverter()

    YamlFlowBuilder(def dsl) {
        this(dsl, DEFAULT_CONFIG_FILE)
    }

    YamlFlowBuilder(def dsl, String configFile) {
        PipelineDslHolder.dsl = dsl
        this.configFile = configFile
    }

    Flow build() {
        dsl.echo "YamlFlowBuilder used configFile: ${configFile}"
        String yamlConfiguration = dsl.readTrusted configFile
        dsl.echo "yamlConfiguration: ${yamlConfiguration}"
        YamlConfig model = getYamlModelUsingJenkinsReadYamlCommand(yamlConfiguration)
        dsl.echo "YamlConfig:build:model: ${model}"
        FlowBuilder flowBuilder = flowBuilderConverter.toFlowBuilder(model)
        dsl.echo "flowBuilder: ${flowBuilder.toString()}"
        return flowBuilder.build()
    }

    YamlConfig getYamlModelUsingJenkinsReadYamlCommand(String yamlConfiguration) {
        dsl.echo "yamlConfiguration before replaceVariables: ${yamlConfiguration}"
        String yamlConfig = replaceVariables(yamlConfiguration)
        dsl.echo "yamlConfig after replaceVariables: ${yamlConfig}"
        def properties = dsl.readYaml text: yamlConfig
        jenkinsBuildParamsConverter.populateWithJenkinsBuildParams(properties)
        YamlConfig result = yamlConverter.toYamlModel(properties)
        Logger.info("YamlFlowBuilder:getYamlModelUsingJenkinsReadYamlCommand:result: ${result}")
        return result
    }

    String replaceVariables(String yamlConfiguration) {
        Logger.info("YamlFlowBuilder:replaceVariables:yamlConfiguration: ${yamlConfiguration}")
        Map binding = dsl.params // variables map to be replaced
        Logger.info("YamlFlowBuilder:replaceVariables: ${binding}")
        def engine = new GStringTemplateEngine()
        def template = engine.createTemplate(yamlConfiguration).make(binding)
        String result = template.toString()
        Logger.info("YamlFlowBuilder:replaceVariables:result: ${result}")
        return result
    }

    @Override
    public String toString() {
        return "YamlFlowBuilder{" +
                "configFile='" + configFile + '\'' +
                '}';
    }

}
