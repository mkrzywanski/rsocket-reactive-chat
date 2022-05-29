import { useKeycloak } from '@react-keycloak/web';
import React, { FC, useEffect, useRef, useState } from 'react';
import useStateRef from 'react-usestateref';
import { ChatMessageStore } from '../../lib/chat-server-client/ChatMessageStore';
import { ChatServerClient } from '../../lib/chat-server-client/ChatServerClient';
import { InputMessage } from '../../lib/chat-server-client/InputMessage';
import { JwtAuthUserMetadataProvider } from '../../lib/chat-server-client/JwtAuthUserMetadataProvider';
import { Message } from '../../lib/chat-server-client/Message';
import { SimpleAuthUserMetadataProvider } from '../../lib/chat-server-client/SimpleAuthUserMetadataProvider';
import ChatInputTextBox from '../ChatInputTextBox/ChatInputTextBox';
import ChatList from '../ChatList/ChatList';
import ChatMessagesFeed from '../ChatMessagesFeed/ChatMessagesFeed';
import styles from './ChatWindow.module.css';


interface ChatWindowProps { }

const ChatWindow: FC<ChatWindowProps> = (props: ChatWindowProps) => {

  const [rsocket, setRsocket] = useState<ChatServerClient | null>(null)
  const [chat, setChat, chatRef] = useStateRef("")
  const [a, setA] = useState("")
  const [chats, setChats] = useState(new Set<string>())
  const chatCache = useRef(new ChatMessageStore());
  const [messages, setMessages] = useState(new Array<Message>());
  const [isStreamInitialized, setStreamInitialized] = useState(false)
  const { keycloak } = useKeycloak();

  async function getClient() {
    const client = await ChatServerClient.CreateAsync("localhost", 9090)
    setRsocket(client)
  }

  useEffect(() => {
    console.log("creating client use effect")
    getClient()
  }, [])

  useEffect(() => {
    initializeStream()
  }, [rsocket])

  const initializeStream = () => {
    if (rsocket != null) {
      if (!isStreamInitialized) {
        rsocket?.messageStream(jwtMetadata, (m: Message) => {
          console.log("aaa" + m)
          chatCache.current.putMessageToChat(m.chatRoomId, m)
          setMessages(chatCache.current.get(chatRef.current))
        })
      } else {
        setStreamInitialized(true)
      }
    }
  }

  const authProviderUser1 = new SimpleAuthUserMetadataProvider("user1", "pass")
  const authProviderUser2 = new SimpleAuthUserMetadataProvider("user2", "pass")
  const jwtMetadata = new JwtAuthUserMetadataProvider(keycloak.token || "")
  console.log(keycloak.tokenParsed)

  const createHandler = (event: React.MouseEvent<HTMLButtonElement>) => {
    console.log("create chat")
    rsocket?.createChat(jwtMetadata, chatId => { setChat(chatId); addChat(chatId) })
  }

  const setChatId = (s: string) => {
    setA(s)
  };

  const joinChat = (event: React.MouseEvent<HTMLButtonElement>) => {
    console.log("join chat")
    setChat(a)
    rsocket?.joinChat(jwtMetadata, a, result => console.log(result))
  }

  const addChat = (chatId: string) => {
    setChats(old => {
      const copy = new Set<string>(old);
      copy.add(chatId)
      return copy;
    })
  }

  const addMessage = (m: Message) => {
    setMessages(old => {
      return [...old, m]
    })
  }

  const changeChat = (chatId: string) => {
    console.log(chatId)
    setChat(chatId);
  }

  return (
    <div className={styles.ChatWindow} data-testid="ChatWindow">
      {rsocket ? (
        <div className="chat-container">
          <button onClick={(e) => { rsocket.sendMessage(authProviderUser1, new InputMessage("user1", "content", chat), m => addMessage(m)) }}>Send user1 message</button>
          <button onClick={createHandler}>Create new chat</button>
          <button onClick={joinChat}>User 2 join chat</button>
          <textarea value={a} onChange={(e) => { setA(e.target.value) }}></textarea>
          <ChatList chatList={chats} chatOnClick={changeChat} />
          <ChatMessagesFeed chatId={chat} messages={messages} />
          {/* <ChatInputTextBox send={(content: String) => { rsocket.sendMessage(authProviderUser1, new InputMessage("user1", content, chat), m => { chatCache.current.putMessageToChat(chat, m) }) }} /> */}
          <ChatInputTextBox send={(content: String) => { rsocket.sendMessage(jwtMetadata, new InputMessage(keycloak.tokenParsed?.preferred_username, content, chatRef.current), m => { chatCache.current.putMessageToChat(chat, m) }) }} />
          {/* <ChatInputTextBox send={(content: String) => { rsocket.sendMessage(authProviderUser2, new InputMessage("user2", content, chat), m => { addMessage(m); chatCache.current.putMessageToChat(chat, m) }) }} /> */}
        </div>
      ) : (
        <div>Not Connected</div>
      )}
    </div>
  )

};

export default ChatWindow;
