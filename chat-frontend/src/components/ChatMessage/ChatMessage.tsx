import React, { FC, Fragment } from "react";
import { Card } from "react-bootstrap";
import { Message } from "../../lib/chat-server-client/Message";
import styles from "./ChatMessage.module.css";

export interface ChatMessageProps {
  message: Message;
}

const ChatMessage: FC<ChatMessageProps> = (props: ChatMessageProps) => {
  return (
    <div className={styles.ChatMessage} data-testid="ChatMessage">
      <li
        className="chat-message d-flex justify-content-between mb-4"
        key={props.message.time.toString()}
      >
        <Card>
          <Card.Body>
            <div>
              <strong className="primary-font">
                {props.message.usernameFrom}
              </strong>
              <small className="pull-right text-muted">
                <>
                  <i className="far fa-clock" /> {props.message.time}
                </>
              </small>
            </div>
            <hr />
            <p className="mb-0">{props.message.content}</p>
          </Card.Body>
        </Card>
      </li>
    </div>
  );
};

export default ChatMessage;
