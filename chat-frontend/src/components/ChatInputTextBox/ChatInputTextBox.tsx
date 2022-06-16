import React, { ChangeEvent, ChangeEventHandler, FC, useState } from "react";
import { Button, Form, InputGroup } from "react-bootstrap";
import styles from "./ChatInputTextBox.module.css";

export interface ChatInputTextBoxProps {
  send: (content: string) => void;
}

const ChatInputTextBox: FC<ChatInputTextBoxProps> = (props) => {
  const [text, setText] = useState("");
  const handleTextChange: ChangeEventHandler = (
    event: ChangeEvent<HTMLInputElement>
  ) => setText(event.target.value);

  return (
    <div className="form-group basic-textarea">
      <InputGroup className="mb-3">
        <Form.Control
          size="lg"
          type="text"
          placeholder="Type your message here..."
          value={text}
          onChange={handleTextChange}
          as="textarea"
          rows={2}
          className={styles.textareaNoResize}
        />
        <Button color="info" onClick={(event) => props.send(text)}>
          Send
        </Button>
      </InputGroup>
    </div>
  );
};

export default ChatInputTextBox;
