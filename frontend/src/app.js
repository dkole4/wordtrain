import React, { useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Container } from 'semantic-ui-react'
import 'semantic-ui-css/semantic.min.css'

import { initializeUser } from './store/actions/auth-actions'
import { Main } from './containers/Main'

export const App = () => {
  const dispatch = useDispatch()

  useEffect(() => {
    dispatch(initializeUser())
  }, [dispatch])

  const user = useSelector(state => state.loggedUser)

  return (
    <Container>
      <Main user={user}/>
    </Container>
  )
}