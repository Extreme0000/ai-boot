# AI-Boot

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-blue.svg)](https://docs.spring.io/spring-ai/reference/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

一个基于 Spring Boot 和 Spring AI 构建的智能对话系统框架，提供高度可扩展、模块化的 AI 应用开发基础设施。

## 核心特性

- **模块化架构** - 业务与技术分离，模块间通过接口松耦合
- **多模型支持** - 支持阿里云通义千问、OpenAI 等多种 AI 模型
- **WebSocket 通信** - 实时双向通信，支持流式响应
- **工具调用** - 支持 Function Calling，扩展 AI 能力
- **自动消息路由** - 基于类型的智能消息分发
- **流式响应** - 实时传输 AI 响应，提升用户体验

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- 通义千问 API Key（或其他支持的 AI 服务）

### 安装与运行

1. **克隆项目**

```bash
git clone <repository-url>
cd ai-boot
```

2. **配置环境**

设置环境变量：
```bash
export DASHSCOPE_API_KEY=your-api-key-here
```

或在 `src/main/resources/application.properties` 中配置：
```properties
spring.ai.dashscope.api-key=your-api-key-here
socket.path=/ws
```

3. **构建项目**

```bash
mvn clean install
```

4. **启动应用**

```bash
mvn spring-boot:run
```

服务将在 `http://localhost:8080` 启动，WebSocket 端点为 `ws://localhost:8080/ws`

### 快速测试

使用 WebSocket 客户端连接并发送消息：

```json
{
  "type": "chat",
  "from": "user123",
  "to": "ai",
  "content": "你好，请介绍一下你自己",
  "time": "2025-10-24 10:00:00",
  "msgType": "text"
}
```

## 架构概览

```
┌─────────────────┐
│  WebSocket 层   │  ← 客户端连接
└────────┬────────┘
         │
┌────────▼────────┐
│  消息路由层     │  ← 自动分发消息
└────────┬────────┘
         │
┌────────▼────────┐
│  业务处理层     │  ← MessageHandler 实现
└────────┬────────┘
         │
┌────────▼────────┐
│   AI 模型层     │  ← 多模型支持
└─────────────────┘
```

### 设计思想

> **业务与技术分离设计**：模块之间通过接口进行调用，模块之间相互独立，互不依赖。

## 核心模块

### 1. Communication Module (通信模块)

WebSocket 通信模块提供实时双向通信能力。

**核心组件**：
- `WebSocketConfig` - WebSocket 配置，默认端点 `/ws`
- `MessageRouter` - 消息路由器，自动分发到对应处理器
- `SessionManager` - 会话管理
- `MessagePublisher` - 消息发布

**消息处理流程**：
1. 客户端通过 WebSocket 发送消息
2. `WebSocketHandler` 接收原始消息
3. `MessageRouter` 根据消息类型路由到对应的 `MessageHandler`
4. 处理器处理消息并通过 `MessagePublisher` 返回响应

#### 扩展自定义消息类型

实现 `MessageHandler` 接口即可自动注册到路由系统：

```java
@Service
public class CustomService implements MessageHandler {
    @Override
    public String getType() {
        return "custom";  // 消息类型
    }

    @Override
    public void handleMessage(String userId, BaseMessage message) {
        // 处理自定义消息
    }
}
```

消息实体需继承 [BaseMessage](src/main/java/fun/aiboot/communication/domain/BaseMessage.java)，参考 [ChatMessage](src/main/java/fun/aiboot/communication/domain/ChatMessage.java)。

完整实现参考 [ChatService.java](src/main/java/fun/aiboot/service/ChatService.java)

### 2. Dialogue LLM Module (对话 AI 模块)

提供统一的 AI 模型访问接口，支持多种 AI 服务。

**核心组件**：
- `ChatModelFactory` - 工厂模式创建 AI 模型实例
- `ModelFrameworkType` - 支持的模型类型枚举
- `ToolsGlobalRegistry` - 全局工具注册表
- `GlobalFunction` - 工具调用接口

**支持的模型**：
- 阿里云通义千问 (Dashscope)
- OpenAI（预留扩展）

#### 使用示例

```java
@Autowired
private ChatModelFactory chatModelFactory;

// 获取模型实例
ChatModel model = chatModelFactory.takeChatModel(ModelFrameworkType.dashscope);

// 流式调用
Flux<String> stream = model.stream("你好");
stream.subscribe(chunk -> System.out.print(chunk));
```

#### 工具调用 (Function Calling)

实现 `GlobalFunction` 接口创建自定义工具：

```java
@Component
public class WeatherFunction implements GlobalFunction {
    @Override
    public ToolCallback getFunctionCallTool() {
        return ToolCallback.from(
            "get_weather",
            "获取天气信息",
            this::getWeather
        );
    }

    public String getWeather(String city) {
        return "晴天，25°C";
    }
}
```

工具会自动注册，AI 可在对话中智能调用。

### 3. Business Layer (业务层)

实现具体业务逻辑，如聊天服务、订单处理等。

**核心服务**：
- `ChatService` - 聊天服务，处理对话消息并集成 AI 模型

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.5.6 | 应用框架 |
| Spring AI | 1.0.0 | AI 集成框架 |
| WebSocket | - | 实时通信 |
| Alibaba Dashscope | 1.0.0.3 | 通义千问 SDK |
| Fastjson2 | 2.0.59 | JSON 处理 |
| Lombok | - | 代码简化 |

## 项目结构

```
ai-boot/
├── src/main/java/fun/aiboot/
│   ├── AiBootApplication.java              # 应用入口
│   ├── communication/                       # 通信模块
│   │   ├── config/                         # 配置
│   │   ├── domain/                         # 消息实体
│   │   └── server/                         # 服务端实现
│   ├── dialogue/llm/                       # AI 对话模块
│   │   ├── factory/                        # 模型工厂
│   │   ├── providers/                      # 模型提供者
│   │   └── tool/                           # 工具调用
│   └── service/                            # 业务服务
├── src/main/resources/
│   └── application.properties              # 应用配置
├── TECHNICAL_DOCUMENTATION.md              # 技术文档
├── README.md                               # 项目说明
└── pom.xml                                 # Maven 配置
```

## 文档

- **[技术文档](TECHNICAL_DOCUMENTATION.md)** - 详细的架构设计、API 说明、开发指南
- **[快速开始](#快速开始)** - 安装与配置指南
- **[核心模块](#核心模块)** - 模块功能说明

## 使用场景

- **智能客服系统** - 实时对话，自动回复
- **AI 助手应用** - 支持工具调用的智能助手
- **知识问答系统** - 基于 AI 的问答服务
- **物联网设备对话** - ESP32 等设备的语音交互
- **企业级聊天机器人** - 可扩展的企业对话解决方案

## 路线图

- [x] WebSocket 通信模块
- [x] 阿里云通义千问集成
- [x] 流式响应支持
- [x] Function Calling 工具调用
- [ ] OpenAI 模型集成
- [ ] 对话历史管理
- [ ] 多轮对话上下文
- [ ] 用户身份认证
- [ ] 消息持久化
- [ ] 集群部署支持

## 贡献

欢迎贡献代码、报告问题或提出改进建议！

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

**Built with ❤️ using Spring Boot and Spring AI**
