import React from 'react'
import { useState } from 'react'
import { useDispatch } from 'react-redux'
import { Container, Segment, Form, Button } from 'semantic-ui-react'
import { genSaltSync, hashSync } from 'bcryptjs'

import { useField } from './WordApp'
import { authService } from '../services'
import { setErrorMessage, setNotificationMessage } from '../reducer-notification'
import { login } from '../reducer-user'

import { Notification } from './Notification'

export const LoginForm = () => {
  const dispatch = useDispatch()

  const [username, resetUsername] = useField('text')
  const [password, resetPassword] = useField('password')
  const [registerView, setView] = useState(false)
  
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

  const switchView = () => 
    setView(!registerView)

  return (
    <Container>
      <Notification />
      <Segment>
        { !registerView && 
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
        }
        { registerView && 
          <RegisterForm />
        }
      </Segment>
      <Button fluid onClick={switchView}>
        { registerView 
          ? 'Log in into account'
          : 'Register an account' }
      </Button>
    </Container>
  )
}

export const RegisterForm = () => {
  const dispatch = useDispatch()

  const [username, resetUsername] = useField('text')
  const [password, resetPassword] = useField('password')
  const [confirm, resetConfirm] = useField('password')

  const handleRegister = async (e) => {
    e.preventDefault()
    
    if (confirm.value != password.value) {
      dispatch(setErrorMessage('Passwords are not matching'))
    } else {
      const hashed = hashSync(password.value, genSaltSync())
      const user = await authService.register({
        username: username.value, password: hashed
      })
      if (user === null)
        dispatch(setErrorMessage('Registration did not succeed'))
      else
        dispatch(
          setNotificationMessage('User was successfully registered, now you can log in')
        )
    }
    resetUsername()
    resetPassword()
    resetConfirm()
  }

  return (
    <Form onSubmit={handleRegister}>
      <Form.Field>
        <label>Username</label>
        <input id='username' placeholder='Username' { ...username } />
      </Form.Field>
      <Form.Field>
        <label>Password</label>
        <input id='password' placeholder='Password' { ...password } />
      </Form.Field>
      <Form.Field>
        <label>Confirm password</label>
        <input id='confirm'  placeholder='Confirm password' { ...confirm } />
      </Form.Field>
      <Button positive type='submit'>Register</Button>
    </Form>
  )
}