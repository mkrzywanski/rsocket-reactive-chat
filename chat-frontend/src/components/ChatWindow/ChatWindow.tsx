import React, { FC, useEffect, useRef, useState } from 'react';
import { Flowable, FlowableProcessor } from 'rsocket-flowable';
import { Observable, Subject } from 'rxjs';
import { text } from 'stream/consumers';
import { ChatServerClient } from '../../lib/chat-server-client/ChatServerClient';
import { InputMessage } from '../../lib/chat-server-client/InputMessage';
import { Message } from '../../lib/chat-server-client/Message';
import { SimpleAuthUserMetadataProvider } from '../../lib/chat-server-client/SimpleAuthUserMetadataProvider';
import ChatInputTextBox from '../ChatInputTextBox/ChatInputTextBox.lazy';
import ChatList from '../ChatList/ChatList';
import ChatMessagesFeed from '../ChatMessagesFeed/ChatMessagesFeed';
import styles from './ChatWindow.module.css';


interface ChatWindowProps { }

const ChatWindow: FC<ChatWindowProps> = (props: ChatWindowProps) => {

  // const [chats] = useState(new Array(0))
  // const rsocket = useRef<ChatServerClient | null>(null)
  const [rsocket, setRsocket] = useState<ChatServerClient | null>(null)
  // const [chat, setChat] = useState("")

  useEffect(() => {
    async function getClient() {
      const client = await ChatServerClient.CreateAsync("localhost", 9090)
      setRsocket(client)
    }
    getClient()
  }, [setRsocket])

  // const authProvider = new SimpleAuthUserMetadataProvider("user1", "pass")

  // const createHandler = (event: React.MouseEvent<HTMLButtonElement>) => {
  //   console.log("in handler")
  //   rsocket.current?.createChat(authProvider, c => setChat(c))
  // }

  console.log("rerender")

  const subject = new Subject<String>();

  // const inputMessages: Flowable<String> = new Flowable(subscriber => {
  //   console.log("flowable")
  //   subject.subscribe({
  //     next:  m => subscriber.onNext(m),
  //     complete: () => console.log("done")
  //   })
  // });

  // const flowable : Flowable<string> = new Flowable((subscriber) => {
  //   // lambda is not executed until `subscribe()` is called
  //   const values = ["a", "b", "c", "d"];
  //   subscriber.onSubscribe({
  //     cancel: () => {
  //       /* no-op */
  //     },
  //     request: (n) => {
  //       while (n--) {
  //         if (values.length) {
  //           const next : string = values[0]
  //           // Can't publish values until request() is called
  //           subscriber.onNext(next);
  //         } else {
  //           subscriber.onComplete();
  //           break;
  //         }
  //       }
  //     },
  //   });
  // });

  const just : Flowable<String> = Flowable.just("a", "b")

  const recivedStrings = rsocket?.test(just)

  console.log("received strings " + recivedStrings)
  // console.log("input messages " + inputMessages)

  recivedStrings?.subscribe({
    onNext:  m => console.log(m),
    onSubscribe: s => {console.log("received strings on subscribe"); s.request(3)}
  })


  // const inputMessages : Flowable<InputMessage> = new Flowable(s => {
  //   subject.subscribe(m => s.onNext(m))
  // });

  // const newMessages = rsocket.current?.channel(authProvider, inputMessages)

  // newMessages?.subscribe({
  //   onSubscribe : (sub) => {
  //     sub.request(20)
  //     console.log("channel")
  //   },
  //   onNext : (e) => {
  //     console.log(e)
  //   }
  // })



  return (
    <div className={styles.ChatWindow} data-testid="ChatWindow">
      { rsocket ? (
        <div className="chat-container">
          <button onClick={(e) => {console.log("clicked");subject.next("test")}}>test</button>
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
