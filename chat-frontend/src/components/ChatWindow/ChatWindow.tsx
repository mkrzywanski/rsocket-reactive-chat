import React, { FC, useEffect, useState } from 'react';
import { ChatServerClient } from '../../lib/chat-server-client/ChatServerClient';
import { InputMessage } from '../../lib/chat-server-client/InputMessage';
import { SimpleAuthUserMetadataProvider } from '../../lib/chat-server-client/SimpleAuthUserMetadataProvider';
import styles from './ChatWindow.module.css';


interface ChatWindowProps { }

const ChatWindow: FC<ChatWindowProps> = (props: ChatWindowProps) => {

  const [rsocket, setRsocket] = useState<ChatServerClient | null>(null)
  const [chat, setChat] = useState("")

  useEffect(() => {
    async function getClient() {
      const client = await ChatServerClient.CreateAsync("localhost", 9090)
      setRsocket(client)
    }
    getClient()
  }, [setRsocket])

  const authProviderUser1 = new SimpleAuthUserMetadataProvider("user1", "pass")
  const authProviderUser2 = new SimpleAuthUserMetadataProvider("user2", "pass")

  const createHandler = (event: React.MouseEvent<HTMLButtonElement>) => {
    console.log("in handler")
    rsocket?.createChat(authProviderUser1, c => setChat(c))
  }

  const joinChat = (event: React.MouseEvent<HTMLButtonElement>) => {
    console.log("join chat")
    rsocket?.joinChat(authProviderUser2, chat)
  }

  rsocket?.messageStream(authProviderUser2, (m) => console.log(m))

  return (
    <div className={styles.ChatWindow} data-testid="ChatWindow">
      {rsocket ? (
        <div className="chat-container">
          <button onClick={(e) => { console.log("clicked"); rsocket.sendMessage(authProviderUser1, new InputMessage("user1", "content", chat)) }}>Send user1 message</button>
          <button onClick={createHandler}>Create new chat</button>
          <button onClick={joinChat}>User 2 join chat</button>
        </div>
      ) : (
        <div>Not Connected</div>
      )}
      {/* <ChatList chatList={chats} /> */}
      {/* <ChatMessagesFeed /> */}
      {/* <ChatInputTextBox send={(content) => {subject.next(new InputMessage("user1", content, chat))}} /> */}
      {/* <ChatInputTextBox send={(content) => { subject.next("test") }} /> */}
      {/* <button onClick={createHandler}>Create new chat</button> */}
      {/* <button onClick={(e) => {console.log("clicked");subject.next("test")}}>test</button> */}
    </div>
  )

};

export default ChatWindow;
