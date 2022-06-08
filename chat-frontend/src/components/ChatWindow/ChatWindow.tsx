import { useKeycloak } from "@react-keycloak/web";
import React, { FC, useContext, useEffect, useRef, useState } from "react";
import { Button, Col, Form, InputGroup, Row } from "react-bootstrap";
import useStateRef from "react-usestateref";
import { InputMessage } from "../../lib/api/InputMessage";
import { Message } from "../../lib/api/Message";
import { JwtAuthUserMetadataProvider } from "../../lib/auth/JwtAuthUserMetadataProvider";
import { ChatMessageStore } from "../../lib/chat-server-client/ChatMessageStore";
import { ChatServerClient } from "../../lib/chat-server-client/ChatServerClient";
import { RsocketContext } from "../../lib/chat-server-client/RsocketContext";
import ChatInputTextBox from "../ChatInputTextBox/ChatInputTextBox";
import ChatList from "../ChatList/ChatList";
import ChatMessagesFeed from "../ChatMessagesFeed/ChatMessagesFeed";
import styles from "./ChatWindow.module.css";

export interface ChatWindowProps {
  navbarHeight?: number;
}

const ChatWindow: FC<ChatWindowProps> = ({ navbarHeight = 0 }) => {
  const [chat, setChat, chatRef] = useStateRef("");
  const [joinChatValue, setJoinChat] = useState("");
  const [chats, setChats] = useState(new Set<string>());
  const chatCache = useRef(new ChatMessageStore());
  const [messages, setMessages] = useState(new Array<Message>());
  const { keycloak } = useKeycloak();
  const [isStreamInitialized, setStreamInitialized] = useState(false);
  const rsocket = useContext(RsocketContext);

  const resultHeight = window.innerHeight - navbarHeight;

  const jwtMetadata = new JwtAuthUserMetadataProvider(keycloak.token || "");

  useEffect(() => {
    initializeStream();
  }, [rsocket]);

  const initializeStream = () => {
    if (rsocket != null) {
      if (!isStreamInitialized) {
        rsocket.messageStream(jwtMetadata, (message: Message) => {
          chatCache.current.putMessageToChat(message.chatRoomId, message);
          const messages = chatCache.current.get(chatRef.current);
          console.log("messages " + messages);
          setMessages(messages);
        });
        rsocket.getUserChats(jwtMetadata, (chats) => setChats(chats));
      } else {
        setStreamInitialized(true);
      }
    }
  };

  const createHandler = (event: React.MouseEvent<HTMLButtonElement>) => {
    rsocket?.createChat(jwtMetadata, (chatId) => {
      setChat(chatId);
      addChat(chatId);
    });
  };

  const setChatId = (s: string) => {
    setJoinChat(s);
  };

  const joinChat = (event: React.MouseEvent<HTMLButtonElement>) => {
    console.log("join chat");
    setChat(joinChatValue);
    rsocket?.joinChat(jwtMetadata, joinChatValue, (result) =>
      console.log(result)
    );
  };

  const addChat = (chatId: string) => {
    setChats((old) => {
      const copy = new Set<string>(old);
      copy.add(chatId);
      return copy;
    });
  };

  const addMessage = (m: Message) => {
    setMessages((old) => {
      return [...old, m];
    });
  };

  const changeChat = (chatId: string) => {
    setChat(chatId);
    setMessages(chatCache.current.get(chatId));
  };

  return (
    <div className={styles.ChatWindow} data-testid="ChatWindow">
      {rsocket ? (
        <div style={{ height: resultHeight, position: "relative" }}>
          <Row className="h-100 m-0">
            <Col sm={4} className="h-100 border-end">
              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  // justifyContent: "space-between",
                  height: "100%",
                  // position: "relative",
                  // flex: 1,
                }}
              >
                <ChatList chatList={chats} chatOnClick={changeChat} />
                <div>
                  <InputGroup>
                    <Form.Control
                      size="lg"
                      value={joinChatValue}
                      onChange={(e) => {
                        setJoinChat(e.target.value);
                      }}
                    ></Form.Control>
                    <Button onClick={joinChat}>Join chat</Button>
                  </InputGroup>
                  <Button onClick={createHandler}>Create new chat</Button>
                </div>
              </div>
            </Col>
            <Col sm={8} className="h-100">
              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  // justifyContent: "space-between",
                  height: "100%",
                  // position: "relative",
                  // flex: 1,
                }}
              >
                <ChatMessagesFeed
                  chatId={chat}
                  messages={messages}
                  currentUserName={keycloak?.tokenParsed?.preferred_username}
                />
                <ChatInputTextBox
                  send={(content: String) => {
                    rsocket.sendMessage(
                      jwtMetadata,
                      new InputMessage(
                        keycloak.tokenParsed?.preferred_username,
                        content,
                        chatRef.current
                      ),
                      (m) => {
                        chatCache.current.putMessageToChat(chat, m);
                        setMessages(chatCache.current.get(chat));
                      }
                    );
                  }}
                />
              </div>
            </Col>
          </Row>
        </div>
      ) : (
        <div>Not Connected</div>
      )}
    </div>
  );
};

export default ChatWindow;
