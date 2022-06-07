import { useKeycloak } from "@react-keycloak/web";
import React, { FC } from "react";
import { ListGroup } from "react-bootstrap";
import { Message } from "../../lib/api/Message";
import ChatMessage from "../ChatMessage/ChatMessage";
import styles from "./ChatMessagesFeed.module.css";

export interface ChatMessagesFeedProps {
  chatId: String;
  messages: Array<Message>;
  currentUserName?: string;
}

const ChatMessagesFeed: FC<ChatMessagesFeedProps> = (
  props: ChatMessagesFeedProps
) => {
  return (
    <>
      <h2 className="border-bottom">Current chat {props.chatId}</h2>
      <div
        className={styles.ChatMessagesFeed}
        data-testid="ChatMessagesFeed"
        style={{ overflowY: "scroll", flex: 1 }}
      >
        <ListGroup>
          {props.messages.map((item, index) => (
            <ChatMessage
              message={item}
              key={index}
              currentUserName={props.currentUserName}
            ></ChatMessage>
          ))}
        </ListGroup>
      </div>
    </>
  );
};

export default ChatMessagesFeed;
