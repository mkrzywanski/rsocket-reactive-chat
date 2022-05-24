import React, { FC } from 'react';
import styles from './Nav.module.css';

interface NavProps { }

const Nav: FC<NavProps> = () => {
  return (
    <div className={styles.Nav} data-testid="Nav">
      <div className="top-0 w-full flex flex-wrap">
        <section className="x-auto">
          <nav className="flex justify-between bg-gray-200 text-blue-800 w-screen">
            <div className="px-5 xl:px-12 py-6 flex w-full items-center">
              <h1 className="text-3xl font-bold font-heading">
                Keycloak React AUTH.
              </h1>
              <ul className="hidden md:flex px-4 mx-auto font-semibold font-heading space-x-12">
                <li>
                  <a className="hover:text-blue-800" href="/">
                    Home
                  </a>
                </li>
                <li>
                  <a className="hover:text-blue-800" href="/secured">
                    Secured Page
                  </a>
                </li>
              </ul>
              <div className="hidden xl:flex items-center space-x-5">
                <div className="hover:text-gray-200">
                  <h1>Login</h1>
                </div>
              </div>
            </div>
          </nav>
        </section>
      </div>
    </div>
  );
}

export default Nav;
