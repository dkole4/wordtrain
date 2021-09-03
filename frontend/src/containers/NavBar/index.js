import React, { useEffect, useState } from 'react'
import { useDispatch } from 'react-redux'
import { Menu } from 'semantic-ui-react'
import { Link, useLocation } from 'react-router-dom'

import { logout } from 'store/actions/auth-actions'

export const NavBar = () => {
  const [page, setPage] = useState('')

  const dispatch = useDispatch()
  const location = useLocation()
  
  useEffect(() => {
    setPage(location.pathname.slice(1))
  }, [])

  const switchPage = (newPage) => {
    setPage(newPage)
  }

  const doLogout = () => {
    dispatch(logout())
  }

  return (
    <Menu pointing>
      <Menu.Item
        name='Front Page'
        active={page === ''}
        as={ Link } 
        to='/'
        onClick={() => switchPage('')} />
      <Menu.Item
        name='Words'
        active={page === 'words'}
        as={ Link } 
        to='/words'
        onClick={() => switchPage('words')} />
      <Menu.Item
        name='Train'
        active={page === 'train'}
        as={ Link } 
        to='/train'
        onClick={() => switchPage('train')} />
      <Menu.Item
        name='Search'
        active={page === 'search'}
        as={ Link } 
        to='/search'
        onClick={() => switchPage('search')} />
      <Menu.Item
        disabled
        name='Forum' />
      <Menu.Item
        disabled
        name='Messages' />
      <Menu.Item
        header
        position='right'
        name='Log out'
        as={ Link }
        to='/' 
        onClick={doLogout} />
    </Menu>
  )
}