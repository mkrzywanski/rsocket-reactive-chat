import { useKeycloak } from "@react-keycloak/web";
import React, { FC, useContext, useEffect, useRef, useState } from "react";
import { Button, Col, Form, InputGroup, Row } from "react-bootstrap";
import InfiniteScroll from "react-infinite-scroll-component";
import useStateRef from "react-usestateref";
import { InputMessage } from "../../lib/api/InputMessage";
import { Message } from "../../lib/api/Message";
import { JwtAuthUserMetadataProvider } from "../../lib/auth/JwtAuthUserMetadataProvider";
import { ChatMessageStore } from "../../lib/chat-server-client/ChatMessageStore";
import { RsocketContext } from "../../lib/chat-server-client/RsocketContext";
import ChatInputTextBox from "../ChatInputTextBox/ChatInputTextBox";
import ChatList from "../ChatList/ChatList";
import ChatMessage from "../ChatMessage/ChatMessage";
import styles from "./ChatWindow.module.css";

export interface ChatWindowProps {
  navbarHeight?: number;
  chats: Set<string>;
  setChats: (chats: Set<string>) => void;
}

const ChatWindow: FC<ChatWindowProps> = ({
  navbarHeight = 0,
  chats,
  setChats,
}) => {
  const pageSize = 10;
  const [chat, setChat, chatRef] = useStateRef("");
  const [joinChatValue, setJoinChat] = useState("");
  const chatCache = useRef(new ChatMessageStore());
  const [messages, setMessages] = useState(new Array<Message>());
  const { keycloak, initialized } = useKeycloak();
  const [isStreamInitialized, setStreamInitialized] = useState(false);
  const rsocket = useContext(RsocketContext);
  const messagesEndRef = React.createRef<HTMLDivElement>();

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
          setMessages(messages);
        });
        rsocket.getUserChats(jwtMetadata, (chats) => {
          setChats(chats);
          chats.forEach((c) => {
            rsocket.getMessagesForChatPaged(
              jwtMetadata,
              c,
              { pageSize: pageSize, pageNumber: 1 },
              (m) => {
                chatCache.current.putMessagesToChat(c, m);
              }
            );
          });
        });
        setStreamInitialized(true);
      } else {
        // setStreamInitialized(true);
      }
    }
  };

  const createHandler = (event: React.MouseEvent<HTMLButtonElement>) => {
    rsocket?.createChat(jwtMetadata, (chatId) => {
      addChat(chatId);
    });
  };

  const joinChat = (event: React.MouseEvent<HTMLButtonElement>) => {
    rsocket?.joinChat(jwtMetadata, joinChatValue, (result) => {
      if (result === true) {
        setChat(joinChatValue);
        addChat(joinChatValue);
      }
    });
  };

  const addChat = (chatId: string) => {
    const copy = new Set<string>(chats);
    copy.add(chatId);
    setChats(copy);
  };

  const changeChat = (chatId: string) => {
    setChat(chatId);
    setMessages(chatCache.current.get(chatId));
  };

  const fetchData = () => {
    const messages = chatCache.current.get(chatRef.current);
    const page = messages.length / pageSize + 1;
    rsocket?.getMessagesForChatPaged(
      jwtMetadata,
      chatRef.current,
      { pageSize: pageSize, pageNumber: page },
      (messages) => {
        chatCache.current.putMessagesToChat(chatRef.current, messages);
        setMessages(chatCache.current.get(chatRef.current));
      }
    );
  };

  return (
    <div className={styles.ChatWindow} data-testid="ChatWindow">
      {rsocket && initialized ? (
        <div style={{ height: resultHeight, position: "relative" }}>
          <Row className="h-100 m-0">
            <Col sm={4} className="h-100 border-end">
              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  height: "100%",
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
                  height: "100%",
                }}
              >
                <h2 className="border-bottom">Current chat {chat}</h2>
                <div
                  id="messageFeed"
                  className={styles.ChatMessagesFeed}
                  data-testid="ChatMessagesFeed"
                  style={{
                    overflow: "auto",
                    overflowY: "scroll",
                    display: "flex",
                    flexDirection: "column-reverse",
                    height: "100%",
                  }}
                >
                  <div ref={messagesEndRef} />
                  <InfiniteScroll
                    dataLength={messages.length}
                    next={fetchData}
                    style={{
                      display: "flex",
                      flexDirection: "column-reverse",
                    }}
                    hasMore={true}
                    inverse={true}
                    loader={<h4>Loading...</h4>}
                    scrollableTarget="messageFeed"
                    scrollThreshold={0.7}
                  >
                    {messages.map((item, index) => (
                      <ChatMessage
                        message={item}
                        key={index}
                        currentUserName={
                          keycloak.tokenParsed?.preferred_username
                        }
                      ></ChatMessage>
                    ))}
                  </InfiniteScroll>
                </div>
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
                        messagesEndRef.current?.scrollIntoView(true);
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
