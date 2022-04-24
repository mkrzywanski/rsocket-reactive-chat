import React, { ChangeEvent, ChangeEventHandler, FC, useState } from 'react';
import styles from './ChatInputTextBox.module.css';

interface ChatInputTextBoxProps {
  send : (content : String) => void
}

const ChatInputTextBox: FC<ChatInputTextBoxProps> = (props) => {
  const [text, setText] = useState("");
  const handleTextChange : ChangeEventHandler = (event: ChangeEvent<HTMLInputElement>) => setText(event.target.value);

  return (
    <div className={styles.ChatInputTextBox} data-testid="ChatInputTextBox">
      <div>Input</div>
      <textarea onChange={handleTextChange}></textarea>
      <button onClick={(event) => props.send("a")}>Send</button>
    </div>
  )
};

export default ChatInputTextBox;
