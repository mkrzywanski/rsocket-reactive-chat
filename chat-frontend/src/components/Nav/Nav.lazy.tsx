import React, { lazy, Suspense } from 'react';

const LazyNav = lazy(() => import('./CustomNav'));

const Nav = (props: JSX.IntrinsicAttributes & { children?: React.ReactNode; }) => (
  <Suspense fallback={null}>
    {/* <LazyNav {...props} /> */}
  </Suspense>
);

export default Nav;
