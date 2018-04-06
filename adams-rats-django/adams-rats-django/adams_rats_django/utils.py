def cap(s, max_len):
    """
    Caps a string if it exceeds the defined maximum. Handles None values.

    Based on this stackoverflow post:
    https://stackoverflow.com/a/11602405/4698227
    :param s: the string to cap, can be None
    :type s: str
    :param max_len: the maximum length
    :type max_len: int
    :return: the capped string
    :rtype: str
    """
    if s is None:
        return None
    if len(s) <= max_len:
        return s
    else:
        return s[0:(max_len - 3)] + "..."


def replace_none(s, none_value=""):
    """
    Replaces 'None' strings with the specified value.
    :param s: the string to check if it is None
    :type s: str
    :param none_value: the string to replace None values with
    :type none_value: str
    :return: the processed string
    :rtype: str
    """

    if s is None:
        return none_value
    return s
