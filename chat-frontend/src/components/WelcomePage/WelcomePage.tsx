import React, { FC } from 'react';
import styles from './WelcomePage.module.css';

interface WelcomePageProps {}

const WelcomePage: FC<WelcomePageProps> = () => (
  <div className={styles.WelcomePage} data-testid="WelcomePage">
    WelcomePage Component
  </div>
);

export default WelcomePage;
