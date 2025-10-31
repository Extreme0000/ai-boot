package fun.aiboot.dialogue.llm;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


public interface LLMService {

    String chat(String message);

    Flux<String> stream(String userId, String message);
}
