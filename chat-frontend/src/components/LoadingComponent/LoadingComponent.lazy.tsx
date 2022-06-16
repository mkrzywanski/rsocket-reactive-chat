import React, { lazy, Suspense } from 'react';

const LazyLoadingComponent = lazy(() => import('./LoadingComponent'));

const LoadingComponent = (props: JSX.IntrinsicAttributes & { children?: React.ReactNode; }) => (
  <Suspense fallback={null}>
    <LazyLoadingComponent {...props} />
  </Suspense>
);

export default LoadingComponent;
