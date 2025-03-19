package com.ivy.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.time.Duration;
import java.util.Map;

public class McpClientJavaSdkExamples {
    public static void main(String[] args) {
        StdioClientTransport transport = new StdioClientTransport(
                ServerParameters.builder("npx")
                        .args("-y",
                                "@modelcontextprotocol/server-filesystem",
                                "/Users/fangjie33/ai/claude-mcp-examples")
                        .build(),
                new ObjectMapper()
        );
        try (McpSyncClient client = McpClient.sync(transport)
                .clientInfo(
                        new McpSchema.Implementation("my-client", "1.0.0")
                )
                .capabilities(
                        McpSchema.ClientCapabilities.builder().roots(true).sampling().build()
                )
                .requestTimeout(Duration.ofSeconds(60))
                .build()) {
            McpSchema.InitializeResult initialize = client.initialize();
            System.out.println("client initialized: " + initialize);


            tools(client); // 打印 MCP 工具列表

            createDirectory(client); // 创建目录
            createFile(client); // 创建文件

        }
    }

    public static void tools (McpSyncClient client) {
        McpSchema.ListToolsResult listToolsResult = client.listTools();
        listToolsResult.tools().forEach(System.out::println);
    }

    public static void createDirectory(McpSyncClient client) {
        McpSchema.CallToolRequest callToolRequest = new McpSchema.CallToolRequest(
                "create_directory",
                Map.of("path", "mcp")
        );
        McpSchema.CallToolResult callToolResult = client.callTool(callToolRequest);
        System.out.println(callToolResult.content());
    }

    public static void createFile(McpSyncClient client) {
        McpSchema.CallToolRequest callToolRequest = new McpSchema.CallToolRequest(
                "write_file",
                Map.of("path", "mcp/test.txt", "content", "hello world")
        );
        McpSchema.CallToolResult callToolResult = client.callTool(callToolRequest);
        System.out.println(callToolResult.content());
    }
}
