import React from 'react'
import { 
  BrowserRouter as Router,
  Route, Switch
} from 'react-router-dom'
import { Container } from 'semantic-ui-react'
import PropTypes from 'prop-types'

import { NavBar } from '../NavBar'
import { Notification } from '../Notification'
import { WordList } from '../WordList'
import { TrainingView } from '../TrainingModule'
import { FrontPage } from 'containers/FrontPage'
import { WordSearch } from '../WordSearch'
import { RegistrationForm } from '../RegistrationForm'
import { LoginForm } from '../LoginForm'


export const Main = ({ user }) => {
  return (
    <Container>
      <Notification />
      <Router>
        { user !== null
          ? 
          <>
            <NavBar loggedIn={ true } />
            <Switch>
              <Route path='/words'>
                <WordList user={ user }/>
              </Route>
              
              <Route path='/train'>
                <TrainingView />
              </Route>

              <Route path='/search'>
                <WordSearch />
              </Route>

              <Route path='/'>
                <FrontPage />
              </Route>
            </Switch>
          </>
          : 
          <>
            <NavBar loggedIn={ false } />
            <Switch>
              <Route path='/register'>
                <RegistrationForm />
              </Route>

              <Route path='/'>
                <LoginForm />
              </Route>
            </Switch>
          </>
        }
      </Router>
    </Container>
  )
}

Main.propTypes = {
  user: PropTypes.object
}