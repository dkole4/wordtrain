import React, { useState } from 'react'
import PropTypes from 'prop-types'
import { useSelector, useDispatch } from 'react-redux'
import {
  Table, Button, Input
} from 'semantic-ui-react'

import { useField } from 'utils/useField'
import { setNotificationMessage } from 'store/actions/notification-actions'
import { updateWord, deleteWord } from 'store/actions/word-actions'


export const WordPair = ({ wordPair }) => {
  const [change, setChange] = useState(false)
  const [word] = useField('text', wordPair.word, false)
  const [transl] = useField('text', wordPair.translation, false)

  const dispatch = useDispatch()
  const user = useSelector(state => state.loggedUser)
  
  const switchChange = () => {
    setChange(!change)
  }

  const submitChange = () => {
    dispatch(
      updateWord(
        wordPair.id, 
        user,
        {
          ...wordPair,
          word: word.value,
          translation: transl.value
        }
      )
    )
    switchChange()
    dispatch(
      setNotificationMessage(
        `The word pair ${word.value}/${transl.value} was updated.`
      )
    )
  }

  const deleteWordPair = () => {
    dispatch(deleteWord(user, wordPair.id))
  }

  return (
    <Table.Row>
      <Table.Cell>
        { change                  
          ? <Input { ...word } /> 
          : wordPair.word }
      </Table.Cell>
      <Table.Cell>
        { change
          ? <Input { ...transl } />
          : wordPair.translation }
      </Table.Cell>
      <Table.Cell>
        { wordPair.lang_word} / { wordPair.lang_translation }
      </Table.Cell>
      <Table.Cell> 
        { wordPair.tries 
          ? `${Math.round(wordPair.score / wordPair.tries * 100)} %`
          : 'was not used'
        } 
      </Table.Cell>
      <Table.Cell collapsing textAlign='right'>
        <Button onClick={switchChange}>{ change ? 'Hide' : 'Change' }</Button>
      </Table.Cell>
      <Table.Cell collapsing textAlign='right'>
        <Button positive={change} negative={!change} onClick={ change ? submitChange : deleteWordPair }>
          { change ? 'Submit' : 'Delete' }
        </Button>
      </Table.Cell>
    </Table.Row>
  )
}

WordPair.propTypes = {
  wordPair: PropTypes.object.isRequired
}