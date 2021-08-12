import React from 'react'
import { useState, useEffect } from 'react'
import { useDispatch } from 'react-redux'
import { 
  BrowserRouter as Router,
  Route, Switch
} from 'react-router-dom'
import { Container } from 'semantic-ui-react'
import PropTypes from 'prop-types'

import { initializeWords } from '../reducer-word'

import { Notification } from './Notification'
import { NavBar } from './NavBar'
import { WordList } from './WordList'
import { TrainingView } from './TrainingView'
import { FrontPage } from './FrontPage'
import { WordSearch } from './WordSearch'


export const useField = (type, defaultValue = '', includeReset = true) => {
  const [value, setValue] = useState(defaultValue)

  const onChangeText = (e) => setValue(e.target.value)
  const onChangeDropdown = (e, { value }) => setValue(value)

  const resetValue = () => setValue('')

  const field = [{ type, value }]

  if (type === 'dropdown') {
    field[0].onChange = onChangeDropdown

    if (includeReset)
      return field.concat(resetValue)
    return field
  }

  field[0].onChange = onChangeText
  
  if (includeReset)
    return field.concat(resetValue)
  return field
}

export const WordApp = ({ user }) => {
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

WordApp.propTypes = {
  user: PropTypes.object.isRequired
}