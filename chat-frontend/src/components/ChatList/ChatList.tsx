import React, { FC } from "react";
import { ListGroup } from "react-bootstrap";
import styles from "./ChatList.module.css";

export interface ChatInfo {}

export interface ChatListProps {
  chatList: Set<string>;
  chatOnClick: (chatId: string) => void;
}

const ChatList: FC<ChatListProps> = (props: ChatListProps) => {
  return (
    <>
      <h2 className="border-bottom">Chats</h2>
      <div
        className={styles.ChatList}
        data-testid="ChatList"
        style={{ overflowY: "scroll", flex: 1 }}
      >
        <ListGroup>
          {Array.from(props.chatList).map((item, index) => (
            <ListGroup.Item
              action
              className="indent"
              key={item}
              onClick={(e) => props.chatOnClick(item)}
            >
              {item}
            </ListGroup.Item>
          ))}
        </ListGroup>
      </div>
    </>
  );
};

export default ChatList;
