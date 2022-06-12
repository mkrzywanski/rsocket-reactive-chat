import React, { FC } from "react";
import { Card } from "react-bootstrap";
import { Message } from "../../lib/api/Message";

export interface ChatMessageProps {
  message: Message;
  currentUserName?: string;
}

const ChatMessage: FC<ChatMessageProps> = (props: ChatMessageProps) => {
  var liStyle = "chat-message d-flex justify-content-between pe-2";

  if (props.message.usernameFrom === props.currentUserName) {
    liStyle += " float-end";
  }

  return (
    <div className="mb-2" data-testid="ChatMessage">
      <li className={liStyle} key={props.message.time.toString()}>
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
