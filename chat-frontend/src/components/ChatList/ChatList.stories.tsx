/* eslint-disable */
import ChatList from './ChatList';

export default {
  title: "ChatList",
};

export const Default = () => <ChatList chatList={new Set()} chatOnClick={(e) => {}}/>;

Default.story = {
  name: 'default',
};
