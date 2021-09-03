import React from 'react'
import { useDispatch } from 'react-redux'
import { Form, Button } from 'semantic-ui-react'

import { useField } from 'utils/useField'
import { authService } from 'services/auth-service'
import {
  setErrorMessage, setNotificationMessage 
} from 'store/actions/notification-actions'

export const RegistrationForm = () => {
  const dispatch = useDispatch()

  const [username, resetUsername] = useField('text')
  const [password, resetPassword] = useField('password')
  const [confirm, resetConfirm] = useField('password')

  const handleRegister = async (e) => {
    e.preventDefault()
    
    if (confirm.value != password.value) {
      dispatch(setErrorMessage('Passwords are not matching'))
    } else {
      const user = await authService.register({
        username: username.value, password: password.value
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