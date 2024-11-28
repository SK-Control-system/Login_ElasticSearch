package ES_test.laon.ES_test.config;

import lombok.Getter;



@Getter
public class ElasticSearchClientFactory extends ElasticSearchClientFactoryConfig {
    public ElasticSearchClientFactory(String pathInfo) {
        super(pathInfo);
    }
}
