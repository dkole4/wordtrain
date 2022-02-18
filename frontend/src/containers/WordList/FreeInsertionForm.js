import React from 'react'
import { useDispatch } from 'react-redux'
import PropTypes from 'prop-types'

import { Table, Button } from 'semantic-ui-react'

import { useField } from 'utils/useField'
import { setErrorMessage } from 'store/actions/notification-actions'

export const FreeInsertionForm = ({ addMany, resetWordList }) => {
  const [words, resetWords] = useField('text')
  const dispatch = useDispatch()

  const addCurrentWords = () => {
    resetWordList()
    const newWords = words.value
      .replace(/\n/g, '')
      .split(';')
      .reduce((arr, word) => {
        if (/[A-Za-z]-[A-Za-z]/.test(word)) {
          return arr.concat(word)
        } else {
          return arr
        }
      }, [])
    
    console.log(newWords)
    const filteredWords = [...new Set(newWords)]
      .map(word => {
        const parts = word.split('-')
        return { word: parts[0], translation: parts[1] }
      })
    
    if (filteredWords.length === 0) {
      dispatch(setErrorMessage('No words were found from input'))
    } else {
      addMany(filteredWords)
    }
    resetWords()
  }

  return (
    <>
      <Table.Row>
        <Table.Cell textAlign='center'>
          <Button circular icon='plus' onClick={addCurrentWords} type='button'/>
        </Table.Cell>
        <Table.Cell colSpan='3'>
          <textarea
            rows='8'
            placeholder='Enter words in format word-translation;word-translation;...'
            { ...words } />
        </Table.Cell>
      </Table.Row>
    </>
  )
}

FreeInsertionForm.propTypes = {
  addMany: PropTypes.func.isRequired,
  resetWordList: PropTypes.func.isRequired
}