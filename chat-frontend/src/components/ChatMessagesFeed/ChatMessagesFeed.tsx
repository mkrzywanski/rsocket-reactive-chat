import { useKeycloak } from "@react-keycloak/web";
import React, { FC, useContext } from "react";
import { ListGroup } from "react-bootstrap";
import { Message } from "../../lib/api/Message";
import ChatMessage from "../ChatMessage/ChatMessage";
import styles from "./ChatMessagesFeed.module.css";
import InfiniteScroll from "react-infinite-scroll-component";
import { RsocketContext } from "../../lib/chat-server-client/RsocketContext";

export interface ChatMessagesFeedProps {
  chatId: String;
  messages: Array<Message>;
  currentUserName?: string;
}

const ChatMessagesFeed: FC<ChatMessagesFeedProps> = (
  props: ChatMessagesFeedProps
) => {

  const rsocket = useContext(RsocketContext)

  return (
    <>
      <h2 className="border-bottom">Current chat {props.chatId}</h2>
      <div
        className={styles.ChatMessagesFeed}
        data-testid="ChatMessagesFeed"
        style={{ overflowY: "scroll", flex: 1 }}
      >
        <ListGroup>
        <InfiniteScroll
            dataLength={props.messages.length}
            next={() => {}}
            hasMore={true}
            inverse={true}
            loader={<h4>Loading...</h4>}
            scrollableTarget="scrollableDiv"
          >
          {props.messages.map((item, index) => (
            <ChatMessage
              message={item}
              key={index}
              currentUserName={props.currentUserName}
            ></ChatMessage>
          ))}
          </InfiniteScroll>
        </ListGroup>
      </div>
    </>
  );
};

export default ChatMessagesFeed;
