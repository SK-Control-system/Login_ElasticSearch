package ES_test.laon.ES_test;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application { //implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

//	@Override
//	public void run(String... args) {
//		checkElasticSearchConnection(); //es 연결 확인
//		putDataToElasticsearch(); // 데이터 삽입 테스트
//	}
//
//	private void checkElasticSearchConnection() {
//		try (RestClient client = new ElasticSearchClientFactory("jj").getEsClient()) {
//			// HEAD 요청으로 Elasticsearch 연결 확인
//			Request request = new Request("HEAD", "/");
//			Response response = client.performRequest(request);
//
//			// 응답 상태 코드 확인
//			if (response.getStatusLine().getStatusCode() == 200) {
//				System.out.println("Elasticsearch 연결 성공!");
//			} else {
//				System.out.println("Elasticsearch 연결 실패!");
//				System.out.println("응답 상태 코드: " + response.getStatusLine().getStatusCode());
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("Elasticsearch 연결 확인 중 오류 발생: " + e.getMessage());
//		}
//	}
//
//	private void putDataToElasticsearch() {
//		try (RestClient client = new ElasticSearchClientFactory("jj").getEsClient()) {
//			// PUT 요청 생성
//			String indexName = "test-index"; // 생성할 또는 사용할 인덱스 이름
//			String documentId = "1";         // 문서 ID
//			String jsonData = """
//                {
//                    "name": "John Doe",
//                    "age": 30,
//                    "email": "john.doe@example.com"
//                }
//            """; // JSON 형식의 데이터
//
//			Request request = new Request("PUT", "/" + indexName + "/_doc/" + documentId);
//			request.setJsonEntity(jsonData); // JSON 데이터를 요청 본문에 설정
//
//			// 요청 실행
//			Response response = client.performRequest(request);
//
//			// 결과 출력
//			System.out.println("Elasticsearch 응답: " + EntityUtils.toString(response.getEntity()));
//			System.out.println("응답 코드: " + response.getStatusLine().getStatusCode());
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("Elasticsearch에 데이터 삽입 중 오류 발생: " + e.getMessage());
//		}
//	}
}
