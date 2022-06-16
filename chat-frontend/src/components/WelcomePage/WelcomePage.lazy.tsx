import React, { lazy, Suspense } from 'react';

const LazyWelcomePage = lazy(() => import('./WelcomePage'));

const WelcomePage = (props: JSX.IntrinsicAttributes & { children?: React.ReactNode; }) => (
  <Suspense fallback={null}>
    <LazyWelcomePage {...props} />
  </Suspense>
);

export default WelcomePage;
