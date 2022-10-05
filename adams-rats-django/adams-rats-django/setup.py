from setuptools import setup, find_packages

setup(
    name="adams-rats-django",
    version="22.10.0",
    license="GNU General Public License version 3.0 (GPLv3)",
    description="Django-based framework for remote control of ADAMS Rat actors.",
    author="Peter Reutemann",
    author_email="fracpete@waikato.ac.nz",
    packages=find_packages(),
    install_requires=[
        "Django >= 4.1, < 4.2",
        "jinja2",
        "requests",
    ],
    classifiers=[
        "Development Status :: 4 - Beta",
        "License :: OSI Approved :: GNU General Public License (GPL)",
        "Programming Language :: Python :: 3",
        "Environment :: Web Environment",
        "Operating System :: OS Independent",
        "Framework :: Django",
    ],
)
