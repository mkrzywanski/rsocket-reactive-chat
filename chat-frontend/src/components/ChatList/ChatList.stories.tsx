/* eslint-disable */
import ChatList from './ChatList';

export default {
  title: "ChatList",
};

export const Default = () => <ChatList chatList={new Array(0)}/>;

Default.story = {
  name: 'default',
};
