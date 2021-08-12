import React from 'react'
import { useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { WordApp } from './components/WordApp'
import { LoginForm } from './components/LoginForm'
import { initializeUser } from './reducer-user'
import { Container } from 'semantic-ui-react'
import 'semantic-ui-css/semantic.min.css'


export const App = () => {
  const dispatch = useDispatch()

  useEffect(() => {
    dispatch(initializeUser())
  }, [dispatch])

  const user = useSelector(state => state.loggedUser)

  return (
    <Container>
      { user === null && <LoginForm /> }
      { user !== null && <WordApp user={user}/> }
    </Container>
  )
}