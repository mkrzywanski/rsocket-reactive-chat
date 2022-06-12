import React, { FC } from 'react';
import styles from './LoadingComponent.module.css';

interface LoadingComponentProps {}

const LoadingComponent: FC<LoadingComponentProps> = () => (
  <div className={styles.LoadingComponent} data-testid="LoadingComponent">
    LoadingComponent Component
  </div>
);

export default LoadingComponent;
