import React, { useEffect } from 'react'
import { useDispatch } from 'react-redux'
import { 
  BrowserRouter as Router,
  Route, Switch
} from 'react-router-dom'
import { Container } from 'semantic-ui-react'
import PropTypes from 'prop-types'

import { initializeWords } from 'store/actions/word-actions'

import { NavBar } from '../NavBar'
import { Notification } from '../Notification'
import { WordList } from '../WordList'
import { TrainingView } from '../TrainingModule'
import { FrontPage } from 'components/FrontPage'
import { WordSearch } from '../WordSearch'


export const WordTrain = ({ user }) => {
  const dispatch = useDispatch()

  useEffect(() => {
    dispatch(initializeWords( user ))
  }, [dispatch])

  return (
    <Container>
      <Router>
        <NavBar />
        <Notification />
        <Switch>
          <Route path='/words'>
            <WordList />
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
      </Router>
    </Container>
  )
}

WordTrain.propTypes = {
  user: PropTypes.object
}