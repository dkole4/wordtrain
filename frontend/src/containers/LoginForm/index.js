import React from 'react'
import { useDispatch } from 'react-redux'
import { Container, Segment, Form, Button } from 'semantic-ui-react'

import { useField } from 'utils/useField'
import { authService } from 'services/auth-service'
import { setErrorMessage } from 'store/actions/notification-actions'
import { login } from 'store/actions/auth-actions'


export const LoginForm = () => {
  const dispatch = useDispatch()

  const [username, resetUsername] = useField('text')
  const [password, resetPassword] = useField('password')
  
  const handleSubmit = async (e) => {
    e.preventDefault()

    const response = await authService.auth(
      { username: username.value, password: password.value }
    )
    
    resetUsername()
    resetPassword()
    if (!response.data) {
      dispatch(setErrorMessage('Invalid credentials'))
    } else {
      dispatch(login(response.data))
    }
  }

  return (
    <Container>
      <Segment>
        <Form onSubmit={handleSubmit}>
          <Form.Field>
            <label>Username</label>
            <input placeholder='Username' id='username' { ...username } />
          </Form.Field>
          <Form.Field>
            <label>Password</label>
            <input placeholder='Password' id='password' { ...password } />
          </Form.Field>
          <Button positive type='submit'>Login</Button>
        </Form>
      </Segment>
    </Container>
  )
}
