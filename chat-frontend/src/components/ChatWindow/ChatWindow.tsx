import React, { FC, useEffect, useRef, useState } from 'react';
import useStateRef from 'react-usestateref';
import { ChatMessageStore } from '../../lib/chat-server-client/ChatMessageStore';
import { ChatServerClient } from '../../lib/chat-server-client/ChatServerClient';
import { InputMessage } from '../../lib/chat-server-client/InputMessage';
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
  const [chats, setChats] = useState(new Set<string>())
  const chatCache = useRef(new ChatMessageStore());
  const [messages, setMessages] = useState(new Array<Message>());
  const [isStreamInitialized, setStreamInitialized] = useState(false)

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
        rsocket?.messageStream(authProviderUser2, (m: Message) => {
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

  const createHandler = (event: React.MouseEvent<HTMLButtonElement>) => {
    console.log("create chat")
    rsocket?.createChat(authProviderUser1, chatId => { setChat(chatId); addChat(chatId) })
  }

  const joinChat = (event: React.MouseEvent<HTMLButtonElement>) => {
    console.log("join chat")
    rsocket?.joinChat(authProviderUser2, chat, result => console.log(result))
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
    setChat(chatId);
  }

  return (
    <div className={styles.ChatWindow} data-testid="ChatWindow">
      {rsocket ? (
        <div className="chat-container">
          <button onClick={(e) => { rsocket.sendMessage(authProviderUser1, new InputMessage("user1", "content", chat), m => addMessage(m)) }}>Send user1 message</button>
          <button onClick={createHandler}>Create new chat</button>
          <button onClick={joinChat}>User 2 join chat</button>
          <ChatList chatList={chats} chatOnClick={changeChat} />
          <ChatMessagesFeed chatId={chat} messages={messages} />
          <ChatInputTextBox send={(content: String) => { rsocket.sendMessage(authProviderUser1, new InputMessage("user1", content, chat), m => { chatCache.current.putMessageToChat(chat, m) }) }} />
          {/* <ChatInputTextBox send={(content: String) => { rsocket.sendMessage(authProviderUser2, new InputMessage("user2", content, chat), m => { addMessage(m); chatCache.current.putMessageToChat(chat, m) }) }} /> */}
        </div>
      ) : (
        <div>Not Connected</div>
      )}
    </div>
  )

};

export default ChatWindow;
