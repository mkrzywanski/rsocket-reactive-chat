/* eslint-disable */
import React from 'react';
import Nav from './CustomNav';

export default {
  title: "Nav",
};

export const Default = () => <Nav setHeight={() => undefined}/>;

Default.story = {
  name: 'default',
};
